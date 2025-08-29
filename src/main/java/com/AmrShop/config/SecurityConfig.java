package com.AmrShop.config;

import com.AmrShop.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Value("${spring.security.user.name}")
    private String inMemoryUsername;

    @Value("${spring.security.user.password}")
    private String inMemoryPassword;

    @Value("${spring.security.user.roles}")
    private String inMemoryRoles;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ Create an in-memory user manager from application.properties
    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager(PasswordEncoder encoder) {
        UserDetails user = User.withUsername(inMemoryUsername)
                .password(encoder.encode(inMemoryPassword))
                .roles(inMemoryRoles.split(","))
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    // ✅ Merge DB + in-memory users
    
    @Bean
    @Primary
    public UserDetailsService userDetailsService(InMemoryUserDetailsManager inMemoryUserDetailsManager) {
        return username -> {
            try {
                return customUserDetailsService.loadUserByUsername(username);
            } catch (UsernameNotFoundException ex) {
                return inMemoryUserDetailsManager.loadUserByUsername(username);
            }
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
