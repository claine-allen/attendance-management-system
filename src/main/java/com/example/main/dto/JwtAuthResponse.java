package com.example.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for JWT authentication response.
 * Contains the JWT token and user details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthResponse {
    private String token;
    private UserDTO user; // Include basic user info
    private String role; // Role as string
}

