package com.ciblorgasport.participantsservice.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * Empêche Spring Boot d'enregistrer AuthTokenFilter une seconde fois
     * comme filtre servlet autonome (en plus de la chaîne Spring Security).
     * Sans ça, le filtre tourne avant la chaîne de sécurité, pose l'auth,
     * puis Spring Security réinitialise le contexte avant de retenter le filtre —
     * qui voit l'attribut "already filtered" et ne pose plus l'auth → 403.
     */
    @Bean
    public FilterRegistrationBean<AuthTokenFilter> jwtFilterRegistration(AuthTokenFilter filter) {
        FilterRegistrationBean<AuthTokenFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable()) // CORS géré par le gateway
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
            	    .requestMatchers("/internal/**").permitAll()
            	    .requestMatchers("/commissaire/epreuves/**", "/api/commissaire/epreuves/**").permitAll()
            	    .requestMatchers("/epreuves/**").permitAll()
            	    .requestMatchers("/error").permitAll()
            	    .anyRequest().authenticated()
            	)
            .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
