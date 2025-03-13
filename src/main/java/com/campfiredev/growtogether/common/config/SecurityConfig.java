package com.campfiredev.growtogether.common.config;

import com.campfiredev.growtogether.member.filter.JwtAuthenticationFilter;
import com.campfiredev.growtogether.member.handler.JwtAccessDeniedHandler;
import com.campfiredev.growtogether.member.util.JwtUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
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
            "/member/register",
            "/member/memberLogin",
            "/ws-chat/**",
            "/topic/**",
            "/queue/**",
            "/app/**",
            "/user/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2 -> { }
                )
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                // 퍼블릭 URL은 인증 없이 허용
                                .requestMatchers("/ws-chat/**","/topic/**","/app/**").permitAll()
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}