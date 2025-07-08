package com.example.main.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a user in the attendance management system.
 * This entity maps to the 'users' table in the database.
 * It contains common user information and their assigned role.
 */
@Entity
@Table(name = "users") // Maps to the 'users' table
@Data // Lombok annotation to generate getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Lombok annotation to generate a no-argument constructor
@AllArgsConstructor // Lombok annotation to generate an all-argument constructor
public class User {
    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures auto-incrementing ID
    private Long id; // Unique identifier for the user

    @Column(nullable = false, unique = true) // Column 'email' cannot be null and must be unique
    private String email; // User's email address, used for login

    @Column(nullable = false) // Column 'password' cannot be null
    private String password; // Hashed password of the user

    @Column(nullable = false) // Column 'role' cannot be null
    @Enumerated(EnumType.STRING) // Stores the enum name (e.g., "ADMIN", "TEACHER", "STUDENT") as a string
    private Role role; // User's role (e.g., ADMIN, TEACHER, STUDENT)

    @Column(name = "first_name", nullable = false) // Maps to 'first_name' column, cannot be null
    private String firstName; // User's first name

    @Column(name = "last_name", nullable = false) // Maps to 'last_name' column, cannot be null
    private String lastName; // User's last name

    @Column(name = "is_active", nullable = false) // Maps to 'is_active' column, cannot be null
    private boolean isActive = true; // Account status, default to true (active)

}
