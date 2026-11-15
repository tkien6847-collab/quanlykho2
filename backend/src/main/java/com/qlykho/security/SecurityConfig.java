package com.qlykho.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler((request, response, exception) ->
                                response.sendError(HttpServletResponse.SC_FORBIDDEN)))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                HttpMethod.GET,
                                "/",
                                "/index.html",
                                "/favicon.ico",
                                "/assets/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/fonts/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/danh-muc/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/nha-cung-cap/**")
                        .hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/api/phieu-nhap/**")
                        .hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")
                        .requestMatchers(HttpMethod.PATCH, "/api/phieu-nhap/**")
                        .hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/tai-khoan/nhan-vien").hasRole("MANAGER")
                        .requestMatchers("/api/tai-khoan/**").hasRole("ADMIN")
                        .requestMatchers("/api/thong-ke/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/nhan-vien/**").hasRole("MANAGER")
                        .requestMatchers("/api/nha-cung-cap/**", "/api/khach-hang/**").hasRole("MANAGER")
                        .requestMatchers("/api/hang-hoa/**").hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")
                        .requestMatchers(HttpMethod.POST, "/api/phieu-nhap/**", "/api/phieu-xuat/**").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.PATCH, "/api/phieu-nhap/**", "/api/phieu-xuat/**").hasRole("MANAGER")
                        .requestMatchers("/api/phieu-nhap/**", "/api/phieu-xuat/**").hasAnyRole("MANAGER", "EMPLOYEE")
                        .requestMatchers("/api/chi-tiet-phieu-nhap/**", "/api/chi-tiet-phieu-xuat/**")
                        .hasAnyRole("MANAGER", "EMPLOYEE")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origin:http://localhost:5173}") String allowedOrigin
    ) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigin));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
