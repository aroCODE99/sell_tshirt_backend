package com.aro.Security;

import com.aro.Entity.AppUsers;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {
    private final AppUsers user;

    public CustomUserDetails(AppUsers user) {
        this.user = user;
    }

    public Long getUserId() {
        return user.getId();
    }

    public String getRealUsername() { return user.getUsername(); }

    public AppUsers getUser() {
        return this.user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getUserRoles().stream()
            .map((role) -> new SimpleGrantedAuthority(role.getRoleName()))
            .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CustomUserDetails{");
        sb.append("user=").append(user);
        sb.append('}');
        return sb.toString();
    }
}
