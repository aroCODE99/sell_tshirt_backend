package com.aro.Services;

import com.aro.DTOs.ErrorResponse;
import com.aro.DTOs.LoginDto;
import com.aro.DTOs.RegisterDto;
import com.aro.DTOs.SuccessResponse;
import com.aro.Entity.*;
import com.aro.Enums.RoleNames;
import com.aro.Exceptions.EmailAlreadyExistsException;
import com.aro.Exceptions.TokenExpiredException;
import com.aro.Repos.*;
import com.aro.Security.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final AuthenticationManager authenticationManager;

    private final RefreshTokenService refreshTokenService;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final AuthRepo authRepo;

    private final RoleRepo roleRepo;

    private final CartRepo cartRepo;

    private final WishListRepo wishListRepo;

    public AuthService(AuthenticationManager authManager, RefreshTokenService refreshTokenService,
                       JwtService jwtService, PasswordEncoder passwordEncoder, AuthRepo authRepo, RoleRepo roleRepo, CartRepo cartRepo, WishListRepo wishListRepo) {
        this.authenticationManager = authManager;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authRepo = authRepo;
        this.roleRepo = roleRepo;
        this.cartRepo = cartRepo;
        this.wishListRepo = wishListRepo;
    }

    public ResponseEntity<?> register(RegisterDto user) {
        if (authRepo.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        // Encode password only
        user.setPassword(passwordEncoder.encode(user.getPassword().strip()));

        Set<Roles> userRoles = Set.of(roleRepo.findByRoleName(RoleNames.USER.name()).orElseThrow(
            () -> new EntityNotFoundException("User Role not found")
        ));

        try {
            // now to initialize the cart
            AppUsers userToSave = new AppUsers(user.getEmail(), user.getUsername(), user.getPassword(), userRoles);
            userToSave.addCart(new Cart()); // add the cart
            userToSave.addWishList(new WishList()); // add the wishList

            authRepo.save(userToSave);

            // now let's initialize them
            return ResponseEntity.ok(new SuccessResponse("User registered SuccessFully", LocalDateTime.now().toString()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("SERVER_ERROR", e.getMessage(), LocalDateTime.now().toString()));
        }
    }

    public ResponseEntity<?> login(LoginDto loginData, HttpServletResponse http) {
        // validating the form
        if (loginData.getPassword() == null || loginData.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password cannot be empty"));
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            loginData.getEmail(), loginData.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails customUserDetailsService = (CustomUserDetails) authentication.getPrincipal();
        RefreshToken refreshToken = refreshTokenService.generateAndSaveRefreshToken(customUserDetailsService);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken.getToken())
            .httpOnly(true)
            .secure(false)
            .maxAge(24 * 60 * 60)
            .path("/")
            .build();

        http.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(Map.of("token", jwtService.generateToken(customUserDetailsService))); // this will need the userDetails
    }

    public Optional<AppUsers> getByUserEmail(String email) {
        return authRepo.findByEmail(email);
    }

    public ResponseEntity<?> logout(HttpServletResponse res) {
        Cookie cookie = new Cookie("refresh_token", "");
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);

        res.addCookie(cookie);
        return ResponseEntity.ok().body("Logout successfully");
    }

    public ResponseEntity<?> getAccessToken(String token) throws TokenExpiredException {
        RefreshToken refreshToken = refreshTokenService.isTokenThere(token);
        AppUsers user = refreshToken.getUser();
        String jwtToken = jwtService.generateToken(user);
        log.info("GIVING_THE_ACCESS_TOKEN...");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("token", jwtToken));
    }

    public ResponseEntity<List<RefreshToken>> getAllRefreshToken() {
        return ResponseEntity.ok(refreshTokenService.getAllTokens());
    }

    public ResponseEntity<List<AppUsers>> getUsers() {
        return ResponseEntity.ok().body(authRepo.findAll());
    }

}
