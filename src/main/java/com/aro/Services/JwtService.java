package com.aro.Services;

import com.aro.Entity.AppUsers;
import com.aro.Security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    @Value("${jwt.secret}")
    private String sk;

    private SecretKey ourSecretKey() {
        byte[] bytes = Base64.getDecoder().decode(sk.getBytes());
        return Keys.hmacShaKeyFor(sk.getBytes());
    }

    public String generateToken(CustomUserDetails userDetails) {
        return Jwts.builder()
            .claim("user_id", userDetails.getUserId())
            .claim("roles", userDetails.getAuthorities())
            .claim("username", userDetails.getRealUsername())
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 15 * 60000))
            .signWith(ourSecretKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public String generateToken(AppUsers user) {
        Collection<? super GrantedAuthority> authorities = user.getUserRoles().stream().map(role ->
            new SimpleGrantedAuthority(role.getRoleName())
        ).collect(Collectors.toSet());

        return Jwts.builder()
            .claim("user_id", user.getId())
            .claim("roles", authorities)
            .claim("username", user.getUsername())
            .setSubject(user.getEmail())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 15 * 60000))
            .signWith(ourSecretKey(), SignatureAlgorithm.HS256)
            .compact();
    }



    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T>T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(ourSecretKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public Date extractExpiry(String token) {
        Claims claims = extractClaims(token);
        return claims.getExpiration();
    }

    public boolean isValid(String token, UserDetails userDetails) {
        return extractSubject(token).equals(userDetails.getUsername()) && extractExpiry(token).after(new Date());
    }

    public Long getUserId(String authHeader) {
        String token = authHeader.substring(7);
        Claims claims = extractClaims(token);
        return Long.parseLong(claims.get("user_id").toString());
    }
}
