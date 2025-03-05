package com.campfiredev.growtogether.common.config;

import com.campfiredev.growtogether.member.filter.JwtAuthenticationFilter;
import com.campfiredev.growtogether.member.oauth2.OAuthLoginSuccessHandler;
import com.campfiredev.growtogether.member.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
//@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    private final OAuthLoginSuccessHandler authLoginSuccessHandler;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil);

        return http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/email/**", "/member/**")
                                .permitAll()
                                .requestMatchers("/logout/kakao", "/user-profile", "/home")
                                .authenticated()
                                .anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2Login -> oauth2Login.successHandler(authLoginSuccessHandler))
                .build();
    }

}
