package com.hotel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/",
                        "/login",
                        "/usuario/registro",
                        "/usuario/registrar",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/img/**",
                        "/webjars/**"
                ).permitAll()
                //Despues de iniciar sesion, cualquier usuario pasa por aqui y se le redirige segun su rol
                .requestMatchers("/habitacion/inicio").authenticated()
                //Todas las funciones de habitacion son solo para el ADMIN
                .requestMatchers("/habitacion/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/habitacion/inicio", true)
                .failureUrl("/login?error")
                .permitAll()
                )
                .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
