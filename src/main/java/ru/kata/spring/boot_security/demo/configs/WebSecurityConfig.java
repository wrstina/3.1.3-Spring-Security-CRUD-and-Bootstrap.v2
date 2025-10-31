package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.kata.spring.boot_security.demo.service.CustomUserDetailService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final SuccessUserHandler successUserHandler;
    private final CustomUserDetailService customUserDetailService;

    public WebSecurityConfig(SuccessUserHandler successUserHandler, CustomUserDetailService customUserDetailService) {
        this.successUserHandler = successUserHandler;

        this.customUserDetailService = customUserDetailService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .userDetailsService(customUserDetailService) //сервис загрузки пользователей
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index").permitAll() // доступно без аутентификации
                        .requestMatchers("/admin/**", "/api/users/**").hasRole("ADMIN") // только для ADMIN
                        .requestMatchers("/user/").hasAnyRole("USER", "ADMIN") // для USER и ADMIN
                        .anyRequest().authenticated() // все остальные запросы требуют аутентификации
                )
                .formLogin(form -> form
                        .successHandler(successUserHandler) // используем кастомный обработчик успешного входа
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { // хэшируем пароли
        return new BCryptPasswordEncoder();
    }
}