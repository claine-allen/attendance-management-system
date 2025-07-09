package com.example.main;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate a BCrypt hashed password.
 * Use this to hash a password for direct insertion into the database via H2 console.
 * This class is for one-time use during development setup.
 */
public class BcryptPasswordGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "password"; // <--- CHANGE THIS to your desired plain-text password
        String hashedPassword = passwordEncoder.encode(rawPassword);

        System.out.println("Raw Password: " + rawPassword);
        System.out.println("Hashed Password: " + hashedPassword);
        System.out.println("\nCopy this Hashed Password to use in your SQL INSERT statement for the H2 console.");
    }
}
