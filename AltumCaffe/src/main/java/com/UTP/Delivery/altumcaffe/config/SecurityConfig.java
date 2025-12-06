package com.UTP.Delivery.altumcaffe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                "/user/contacto/enviar",  
                "/user/carrito/add",
                "/user/carrito/update",
                "/user/carrito/remove",
                "/user/carrito/procesarPagoAjax",
                "/user/aula/save" 
            ))
            .authorizeHttpRequests(authz -> authz

                .requestMatchers(HttpMethod.GET, "/", "/user/home", "/user/compra", "/sobreNosotrosUsuario" , "/contactoUsuario").permitAll()
                .requestMatchers("/*.css", "/*.js", "/css/**", "/js/**", "/assets/**", "/uploads/**").permitAll() 
                .requestMatchers("/login", "/register-ajax").permitAll() 
                .requestMatchers(HttpMethod.GET, "/user/carrito", "/user/aula").authenticated()

                .requestMatchers(HttpMethod.POST, "/user/carrito/**", "/user/aula/save", "/user/reclamacion/enviar", "/user/contacto/enviar").authenticated()

                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")

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