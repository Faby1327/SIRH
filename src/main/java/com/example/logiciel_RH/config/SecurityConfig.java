package com.example.logiciel_RH.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Hachage des mots de passe des comptes utilisateurs.
 *
 * Le projet n'utilise pas encore Spring Security pour les autorisations :
 * seules les fonctions de hachage BCrypt sont utilisées.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
