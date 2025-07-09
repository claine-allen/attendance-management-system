//src/main/java/com/college/attendancemanagementsystem/security/SecurityConfig.java
package com.example.main.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.authentication.AuthenticationManager; // Can comment out if not used
import org.springframework.security.authentication.AuthenticationProvider; // Can comment out if not used
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // Can comment out if not used
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // Can comment out if not used
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy; // Can comment out if not used
import org.springframework.security.crypto.password.PasswordEncoder; // Can comment out if not used
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Can comment out if not used
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security configuration class for the Attendance Management System.
 * Temporarily permits all requests for development, but keeps security beans.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Configures the security filter chain to permit ALL requests.
     * This effectively disables Spring Security's authorization checks,
     * but keeps all necessary security beans in the application context.
     * @param http The HttpSecurity object to configure.
     * @return The configured SecurityFilterChain.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for stateless APIs
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS via our bean
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll() // <<<<<<<<<<<<<<<<<<<<<<<<<<<< CRITICAL CHANGE: Permit ALL requests
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Use stateless sessions (for JWT)
            )
            .authenticationProvider(authenticationProvider()) // Keep this bean
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // Keep this filter
            .headers(headers -> headers.frameOptions().disable()); // Needed for H2-console to be visible

        return http.build();
    }

    /**
     * Defines the authentication provider. Uses DaoAuthenticationProvider with our
     * CustomUserDetailsService and BCryptPasswordEncoder.
     * This bean is kept active even if all requests are permitted.
     * @return The configured AuthenticationProvider.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    /**
     * Exposes the AuthenticationManager bean, which is used for authenticating users.
     * This bean is kept active even if all requests are permitted.
     * @param config AuthenticationConfiguration object.
     * @return The AuthenticationManager.
     * @throws Exception If an error occurs during manager creation.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Super lenient CORS configuration.
     * IMPORTANT: When allowCredentials is true, allowedOrigins cannot contain "*".
     * Explicitly list all known origins for development.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // IMPORTANT CHANGE: Replaced List.of("*") with explicit origins
        // This resolves the "When allowCredentials is true, allowedOrigins cannot contain the special value "*"" error.
        configuration.setAllowedOrigins(List.of(
            "http://localhost:8081",        // React Native web/emulator origin
            "http://192.168.1.4:8081",      // Your specific IP from previous errors (if RN uses it as origin)
            "http://192.168.1.4:8080",      // Your Spring Boot backend's IP and port (if RN uses it as origin)
            "http://10.0.2.2:8080",         // Android Emulator's access to host localhost:8080
            "http://localhost:19000",       // Common Expo dev server port
            "exp://localhost:19000",        // Expo Go app URI for localhost
            "exp://192.168.1.4:19000",      // Example Expo Go app URI on physical device (replace 192.168.1.4 with your actual local IP)
            "http://localhost:3000"         // If you also test with a web browser (e.g., React web app)
        ));
        configuration.setAllowedMethods(Arrays.asList("*")); // Allow all methods
        configuration.setAllowedHeaders(List.of("*")); // Allow all headers
        configuration.setAllowCredentials(true); // Keep this as it's needed for Authorization header
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply to all paths
        return source;
    }
}
