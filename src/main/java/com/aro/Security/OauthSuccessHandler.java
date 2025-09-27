package com.aro.Security;

import com.aro.DTOs.RegisterDto;
import com.aro.Entity.*;
import com.aro.Enums.RoleNames;
import com.aro.Repos.AuthRepo;
import com.aro.Repos.RoleRepo;
import com.aro.Services.AuthService;
import com.aro.Services.JwtService;
import com.aro.Services.RefreshTokenService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

@Service
public class OauthSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OauthSuccessHandler.class);

    private final RefreshTokenService refreshTokenService;

    private final JwtService jwtService;

    private final CustomUserDetailsService userDetailsService;

    private final RoleRepo roleRepo;

    private final AuthRepo authRepo;

    @Value("${frontend.url}")
    private String frontendUrl;

    public OauthSuccessHandler(RefreshTokenService refreshTokenService, JwtService jwtService, CustomUserDetailsService userDetailsService, RoleRepo roleRepo, AuthRepo authRepo) {
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.roleRepo = roleRepo;
        this.authRepo = authRepo;
    }

    // now what should this do
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = token.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String username = Objects.requireNonNull(oAuth2User.getAttribute("given_name")).toString().toLowerCase();
        String oauthProvider = token.getAuthorizedClientRegistrationId();

        CustomUserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(email);
        } catch (Exception e) {
            // does this just registers the use
            log.info("User Not Found {}", e.getMessage());

            // this all happens synchronously but TO OCCUR ASYNC WE COULD USE THE @aSYNC ANNOTATION ON THE ENTITY
            Set<Roles> userRoles = Set.of(roleRepo.findByRoleName(RoleNames.USER.name()).orElseThrow(
                () -> new EntityNotFoundException("User Role not found")
            ));

            AppUsers userToSave = new AppUsers(
                email, username, null, userRoles
            );
            userToSave.addCart(new Cart());
            userToSave.addWishList(new WishList());

            authRepo.save(userToSave);
            userDetails = userDetailsService.loadUserByUsername(email);
        }

            RefreshToken refreshToken = refreshTokenService.generateAndSaveRefreshToken(userDetails);
            String jwtAccessToken = jwtService.generateToken(userDetails);

            // ✅ Correct Set-Cookie header
            ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken.getToken())
                .secure(false) // true in production
                .httpOnly(true)
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();

            response.addHeader("Set-Cookie", cookie.toString());

            // ✅ Send JWT access token to frontend
            String frontendRedirectUrl = frontendUrl+"/oauth/redirect?token=" + jwtAccessToken;
            response.sendRedirect(frontendRedirectUrl);
        }

    }
