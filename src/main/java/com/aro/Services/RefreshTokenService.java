package com.aro.Services;

import com.aro.Entity.AppUsers;
import com.aro.Entity.RefreshToken;
import com.aro.Exceptions.TokenExpiredException;
import com.aro.Repos.AuthRepo;
import com.aro.Repos.RefreshTokenRepo;
import com.aro.Security.CustomUserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepo tokenRepo;

    private final AuthRepo authRepo;

    public RefreshTokenService(RefreshTokenRepo tokenRepo, AuthRepo authRepo) {
        this.tokenRepo = tokenRepo;
        this.authRepo = authRepo;
    }

    private String generateRefreshToken() {
        var secureRandom = new SecureRandom();
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Transactional
    public RefreshToken generateAndSaveRefreshToken(CustomUserDetails userDetails) {
        String token = generateRefreshToken(); // now the generating the token is easy but the hard part is saving

        AppUsers user = authRepo.findById(userDetails.getUserId()).orElseThrow(
            () -> new UsernameNotFoundException("User not present")
        );

        RefreshToken existedToken = tokenRepo.findByUserId(userDetails.getUserId()).orElse(null);

        if (existedToken != null) {
            existedToken.setToken(token);
            return tokenRepo.save(existedToken);
        }

        // create a new token and save
        RefreshToken refreshToken = new RefreshToken(token, user);
        return tokenRepo.save(refreshToken);
    }

    // this just checks if the token is there in the database
    public RefreshToken isTokenThere(String token) throws TokenExpiredException {
        return tokenRepo.findByToken(token).orElseThrow(
            () -> new TokenExpiredException("Refresh token expired")
        );
    }

    public List<RefreshToken> getAllTokens() {
        return tokenRepo.findAll();
    }

}

// i want to become the java god
// now for looking up the data in sql from the row to row is going to be slow
// so you should be indexing the column which you are going to looking up frequently
// indexing means ?
// An index in a database is like an index at the back of a book — it helps the database find data faster.
// EXAMPLE :
//   Without index = You look through every name to find "John"
//   With index = You go straight to the "J" section → "Jo" → "John" ✅

