package org.config;

import org.service.AccountService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.config.JBCryptPasswordEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Sử dụng JBCryptPasswordEncoder để mã hóa mật khẩu với jBCrypt
        return new JBCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
    
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            logger.error("Lỗi xác thực: {}", exception.getMessage());
            // Use ASCII-only characters in the URL to avoid encoding issues
            response.sendRedirect("/login?error=true");
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, DaoAuthenticationProvider authenticationProvider) throws Exception {
        logger.info("Configuring security filter chain");
        
        // Use the authenticationProvider bean instead of configuring UserDetailsService directly
        http.authenticationProvider(authenticationProvider);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Public pages that anyone can access
                        .requestMatchers("/", "/home", "/login", "/register", "/css/**", "/js/**", "/images/**", "/imgs/**", "/car/filter", "/car-details", "/car-details/**").permitAll()
                        
                        // Admin-only pages
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/update-rental-status").hasRole("ADMIN")
                        .requestMatchers("/admin/approve-rental").hasRole("ADMIN")
                        .requestMatchers("/admin/handle-early-return").hasRole("ADMIN")
                        
                        // Customer and admin pages (authenticated users)
                        .requestMatchers("/customer/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/rental-history").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/rental-details").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/request-early-return").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/cancel-rental").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/profile").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/profile/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/rent-car").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/rental-form").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/rent-car-form").hasAnyRole("USER", "ADMIN")
                        
                        // Require authentication for everything else
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureHandler(authenticationFailureHandler())
                        .defaultSuccessUrl("/login-success")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }
}
