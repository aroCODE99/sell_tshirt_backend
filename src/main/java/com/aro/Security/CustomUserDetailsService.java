package com.aro.Security;

import com.aro.Repos.AuthRepo;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthRepo authRepo;

    public CustomUserDetailsService(AuthRepo authRepo) {
        this.authRepo = authRepo;
    }


    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return authRepo.findByEmail(username).map(CustomUserDetails::new).orElseThrow(
            () -> new UsernameNotFoundException("User is not present in the Database")
        );
    }
}
