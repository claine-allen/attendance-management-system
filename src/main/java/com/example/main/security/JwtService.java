package com.example.main.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service for handling JWT (JSON Web Token) operations.
 * This includes generating tokens, extracting claims, and validating tokens.
 */
@Component
public class JwtService {

    // Inject JWT secret key from application.properties
    @Value("${app.jwt.secret}")
    private String SECRET;

    // Inject JWT expiration time from application.properties
    @Value("${app.jwt.expiration}")
    private long JWT_EXPIRATION; // In milliseconds

    /**
     * Extracts the username (subject) from a JWT token.
     * @param token The JWT token.
     * @return The username (email) from the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from a JWT token.
     * @param token The JWT token.
     * @param claimsResolver A function to resolve the desired claim from the Claims object.
     * @param <T> The type of the claim.
     * @return The extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from a JWT token.
     * @param token The JWT token.
     * @return The Claims object containing all token claims.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Validates a JWT token against user details.
     * @param token The JWT token to validate.
     * @param userDetails The UserDetails object representing the authenticated user.
     * @return True if the token is valid for the user and not expired, false otherwise.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Checks if a JWT token has expired.
     * @param token The JWT token.
     * @return True if the token is expired, false otherwise.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from a JWT token.
     * @param token The JWT token.
     * @return The expiration Date of the token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generates a JWT token for a given username.
     * @param username The username (subject) for whom the token is generated.
     * @return The generated JWT token string.
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * Creates the JWT token with given claims and subject.
     * @param claims Additional claims to include in the token.
     * @param username The subject of the token (typically the user's email/username).
     * @return The signed JWT token string.
     */
    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSignKey(), SignatureAlgorithm.HS512) // Using HS512 algorithm
                .compact();
    }

    /**
     * Retrieves the signing key for JWT.
     * @return The Key object for signing and verifying JWTs.
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
