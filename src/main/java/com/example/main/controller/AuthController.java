package com.example.main.controller;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import com.example.main.dto.JwtAuthResponse;
import com.example.main.dto.UserDTO;
import com.example.main.dto.UserLoginRequest;
import com.example.main.dto.UserRegisterRequest;
import com.example.main.exception.DuplicateEntryException;
import com.example.main.model.Role;
import com.example.main.security.CustomUserDetails;
import com.example.main.security.JwtService;
import com.example.main.service.UserService;

/**
 * REST Controller for user authentication and registration.
 */
@RestController
@RequestMapping("/api/v1/auth") // Base path for authentication endpoints
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registers a new user.
     * Accessible to all for initial setup, but can be restricted to ADMIN only later.
     * @param request The DTO containing registration details.
     * @return ResponseEntity with the created UserDTO and HTTP status 201.
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        UserDTO newUser = userService.registerUser(request);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    /**
     * Registers a new ADMIN user. This endpoint is typically for initial system setup
     * and should be secured or removed after the first admin is created in a production environment.
     * @param request The DTO containing registration details for the admin.
     * @return ResponseEntity with the created UserDTO and HTTP status 201.
     */
    @PostMapping("/register/admin")
    public ResponseEntity<UserDTO> registerAdmin(@Valid @RequestBody UserRegisterRequest request) {
        // Force role to ADMIN
        request.setRole(Role.ADMIN);
        UserDTO newUser = userService.registerUser(request);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    /**
     * Authenticates a user and returns a JWT token upon successful login.
     * @param request The DTO containing login credentials.
     * @return ResponseEntity with JwtAuthResponse containing the token and user info.
     * @throws BadCredentialsException if authentication fails.
     */
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody UserLoginRequest request) {
        try {
            // Authenticate the user using Spring Security's AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // If authentication is successful, generate a JWT token
            if (authentication.isAuthenticated()) {
                // Get the CustomUserDetails to retrieve the actual user ID and role
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                Long userId = userDetails.getUserId();
                String email = userDetails.getUsername();
                String role = userDetails.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority).orElse(null);

                String token = jwtService.generateToken(email);

                // Fetch the full UserDTO for the response
                UserDTO userDTO = userService.getUserById(userId);

                return ResponseEntity.ok(new JwtAuthResponse(token, userDTO, role));
            } else {
                // Should not happen if authenticate() doesn't throw, but as a fallback
                throw new BadCredentialsException("Invalid credentials.");
            }
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            // Catch specific authentication exceptions and rethrow as BadCredentialsException
            // This ensures a consistent 401 response for incorrect credentials
            throw new BadCredentialsException("Invalid email or password.", e);
        } catch (DuplicateEntryException e) {
            // This should ideally not happen during login but handle if register endpoint is somehow used for login
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
