package br.com.forumhub.infra.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // API stateless; sem CSRF
                .formLogin(AbstractHttpConfigurer::disable) // sem login form
                .httpBasic(AbstractHttpConfigurer::disable) // sem basic auth
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/topicos").permitAll()
                        .requestMatchers("/topicos/**").permitAll() // liberar tbm futuros GET/PUT/DELETE se necessário
                        .anyRequest().permitAll() // por enquanto, tudo liberado
                );
        return http.build();
    }
}