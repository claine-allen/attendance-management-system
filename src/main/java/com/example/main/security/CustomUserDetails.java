package com.example.main.security;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.main.model.User;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom implementation of Spring Security's UserDetails interface.
 * Adapts our application's User entity to what Spring Security expects.
 */
public class CustomUserDetails implements UserDetails{

    private String username; // In our case, this will be the user's email
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean enabled;
    private Long userId; // To store the actual user ID from our database

    /**
     * Constructor that takes our User entity and converts it to UserDetails.
     * @param user The User entity from our application.
     */
    public CustomUserDetails(User user) {
        this.username = user.getEmail();
        this.password = user.getPassword(); // This is the hashed password
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        this.enabled = user.isActive(); // Account status
        this.userId = user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // We don't have account expiration logic for now
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // We don't have account locking logic for now
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // We don't have credential expiration logic for now
    }

    @Override
    public boolean isEnabled() {
        return enabled; // Based on our user's isActive status
    }

    /**
     * Getter for the actual user ID from our database.
     * @return The user's ID.
     */
    public Long getUserId() {
        return userId;
    }
}
