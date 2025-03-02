package webApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.disable()) // ✅ Disable CORS since we allow all requests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll() // ✅ Allow all API requests
                        .anyRequest().permitAll() // ✅ Allow everything else
                )
                .csrf(csrf -> csrf.disable()) // ✅ Fixes 403 errors for API calls
                .formLogin(form -> form.disable()) // ✅ Disable login form
                .httpBasic(httpBasic -> httpBasic.disable()); // ✅ Disable basic authentication

        return http.build();
    }

    /**
     * ✅ Fix CORS issues when calling APIs from different origins (e.g., localhost:63342)
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of(
                "http://localhost:63342", // ✅ WebStorm Live Preview
                "http://localhost:5500",  // ✅ VS Code Live Server
                "http://localhost:3000",  // ✅ React/Next.js Frontend
                "http://localhost"        // ✅ General localhost
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
