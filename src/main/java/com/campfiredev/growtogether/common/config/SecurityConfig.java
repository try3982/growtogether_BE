package com.campfiredev.growtogether.common.config;

import com.campfiredev.growtogether.member.filter.JwtAuthenticationFilter;
import com.campfiredev.growtogether.member.handler.JwtAccessDeniedHandler;
import com.campfiredev.growtogether.member.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtUtil jwtUtil;

    private static final String[] PUBLIC_URLS = {
            // spring default url
            "/error",
            "/favicon.ico",

            // login & kakao oauth
            "/login",
            "/oauth2/authorization/kakao",
            "/oauth2/code/kakao",
            "/api/email/**",
            "/api/study/**",
            "/member/memberLogin",
            "/payment/**",
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2 -> { }
                )
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                // 퍼블릭 URL은 인증 없이 허용
                                .requestMatchers(PUBLIC_URLS).permitAll()
                                .requestMatchers("/api/bootcamp","/sse").authenticated()
                                // 그 외의 요청은 인증 필요
                                .anyRequest().authenticated()
                )
                .exceptionHandling(config -> config
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .addFilterBefore(new JwtAuthenticationFilter(PUBLIC_URLS, jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}