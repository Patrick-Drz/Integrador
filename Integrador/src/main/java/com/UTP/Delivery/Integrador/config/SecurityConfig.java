package com.UTP.Delivery.Integrador.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.ignoringRequestMatchers(
                "/register-ajax",
                "/user/reclamacion/enviar",
                "/user/carrito/add",
                "/user/carrito/update",
                "/user/carrito/remove",
                "/user/carrito/procesarPagoAjax"
            ))

            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/*.css", "/*.js", "/css/**", "/js/**", "/assets/**", "/uploads/**").permitAll()
                .requestMatchers("/login", "/register-ajax").permitAll()
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/user/**").hasAuthority("ROLE_USER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/welcome", true)
                .failureUrl("/login?error=true")
                .usernameParameter("loginEmail")
                .passwordParameter("loginPassword")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );
        return http.build();
    }
}