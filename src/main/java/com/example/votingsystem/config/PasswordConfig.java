package com.example.votingsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// ✅ Ye class sirf BCrypt Bean banata hai
// SecurityConfig.java ki zarurat NAHI — Spring Security ka login page nahi aayega
@Configuration
public class PasswordConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}