package com.aro.Security;

import com.aro.Services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final CustomUserDetailsService userDetailsService;

    public JWTFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    // now we are going to have the two tokens first one will be the access token and second will be Refresh Token
    // but the thing is that we refresh token can be whatever doesn't have to be the jwt
    // one will be in header and second will be in the cookie
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException
    {
        String authHeader = request.getHeader("Authorization");
        String jwt = "";
        String userEmail = "";

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            userEmail = jwtService.extractSubject(jwt);

            // this getContext().getAuthentication() is for this current request why will we check this even we know
            // when this is going to be null ?
            // Other filters may authenticate first	Prevents overwriting existing auth
            // this should not be null
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                CustomUserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
                if (jwtService.isValid(jwt, userDetails)) {

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities
                    );

                    // this extracts details like the remote address, session ID (if applicable), and other relevant information.
                    // from the incoming request
                    // and this may be helpful for
                    //     -> Adds important request-level metadata (IP, session ID)
                    //     -> Keeps your authentication context in sync with what Spring Security expects
                    //     -> Helps with logging, auditing, and debugging issues
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response);

    }
}
