package com.aro.Controllers;

import com.aro.DTOs.LoginDto;
import com.aro.DTOs.RegisterDto;
import com.aro.Entity.AppUsers;
import com.aro.Services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello this is the first Controller";
    }

    // this is working good
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto userData) {
        return authService.register(userData);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginData, HttpServletResponse servletResponse) {
        try {
            return authService.login(loginData, servletResponse);
        } catch (Exception e) {
            log.error("Login failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("Unauthorized", "User is not present in our DataBase"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse res) {
        return authService.logout(res);
    }

    @PostMapping("/getAccessToken")
    public ResponseEntity<?> getAccessToken(@CookieValue("refresh_token") String token, HttpServletResponse http) {
        try {
            return authService.getAccessToken(token);
        } catch (Exception e) {
            log.error("Error {}", e.getMessage());
            ResponseCookie cookie = ResponseCookie.from("refresh_token" )
                .httpOnly(true)
                .secure(false)
                .maxAge(0)
                .path("/")
                .build();

            http.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("Unauthorized", "User Must be re-logged in"));
        }
    }

    @GetMapping("/getAllTokens")
    public ResponseEntity<?> getAllTokens() {
        return authService.getAllRefreshToken();
    }

    @GetMapping("/getUsers")
    public ResponseEntity<List<AppUsers>> getUsers() {
        return authService.getUsers();
    }

}