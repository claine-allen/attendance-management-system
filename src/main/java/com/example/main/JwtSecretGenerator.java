// src/main/java/com/college/attendancemanagementsystem/util/JwtSecretGenerator.java
package com.example.main;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;

/**
 * Utility class to generate a secure JWT secret key.
 * This class is for one-time use during development setup.
 * The generated key should be stored securely in application.properties or environment variables.
 */
public class JwtSecretGenerator {

    public static void main(String[] args) {
        // Generate a cryptographically secure key for HS512 algorithm
        // HS512 requires a key of at least 64 bytes (512 bits)
        byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded();

        // Encode the key bytes into a Base64 string
        String base64Key = Base64.getEncoder().encodeToString(keyBytes);

        System.out.println("Generated JWT Secret Key (Base64-encoded):");
        System.out.println(base64Key);

        System.out.println("\nCopy this key and paste it into your application.properties file:");
        System.out.println("app.jwt.secret=" + base64Key);
    }
}
