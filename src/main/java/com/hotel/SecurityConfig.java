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
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                // Acceso público
                .requestMatchers(
                        "/",
                        "/login",
                        "/usuario/registro",
                        "/usuario/registrar",
                        "/consultas/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/img/**",
                        "/webjars/**"
                ).permitAll()
                        
                        
                // Redirección por rol después del login
                .requestMatchers("/inicio-rol").authenticated()
                        
                        
                // SUPERADMIN
                .requestMatchers(
                        "/superadmin/**",
                        "/empresa/**",
                        "/usuario/listado",
                        "/usuario/nuevo",
                        "/usuario/guardar",
                        "/usuario/modificar",
                        "/usuario/actualizar",
                        "/usuario/eliminar"
                ).hasRole("SUPERADMIN")
                        
                        
                // ADMIN
                .requestMatchers(
                        "/habitacion/**",
                        "/tipoHabitacion/**",
                        "/servicio/**",
                        "/usuario/clientes"
                ).hasRole("ADMIN")
                        
                        
                // CLIENTE
                .requestMatchers(
                        "/cliente/**"
                ).hasRole("CLIENTE")
                        
                        
                // Cualquier otra ruta requiere sesión
                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/inicio-rol", true)
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
