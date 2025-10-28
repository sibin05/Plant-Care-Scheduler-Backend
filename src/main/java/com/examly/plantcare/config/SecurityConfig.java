package com.examly.plantcare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.config.http.SessionCreationPolicy;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Constructor-based injection of the JWT filter
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // Swagger whitelisted endpoints
    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**"
    };

    // Password encoder for hashing passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security filter chain for handling JWT authentication and CORS
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Disable CSRF for stateless authentication (JWT)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // Apply CORS settings
            .authorizeRequests(auth -> auth
                .antMatchers("/api/auth/**").permitAll()  // Public access to auth routes
                .antMatchers("/api/environment/**").permitAll()  // Public access to environment data routes
                .antMatchers(SWAGGER_WHITELIST).permitAll()  // Public access to Swagger UI
                .antMatchers("/api/plants/**").permitAll() // Restrict access to users with role USER
                .antMatchers("/api/species/**").permitAll() // Restrict access to users with role USER
                .antMatchers("/api/care-tasks/**").permitAll()  // Restrict access to users with role USER
                .antMatchers("/api/tasks/**").permitAll()  // Allow access to task endpoints
                .antMatchers("/api/health-records/**").permitAll()
                .anyRequest().authenticated()  // Authenticate any other request
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)  // Add JWT filter

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Ensure stateless sessions, as JWT will manage state
            );

        return http.build();
    }

    // CORS Configuration for allowing cross-origin requests
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);
        corsConfig.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:5501"));  // Your front-end domains
        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));  // Allowed HTTP methods
        corsConfig.setAllowedHeaders(List.of("Authorization", "Content-Type"));  // Allowed headers for authorization and content type
        corsConfig.setExposedHeaders(List.of("Authorization"));  // Expose Authorization header to front-end for subsequent requests

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);  // Apply globally to all endpoints
        return source;
    }
}
