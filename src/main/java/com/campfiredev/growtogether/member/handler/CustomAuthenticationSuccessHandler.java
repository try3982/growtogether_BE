package com.campfiredev.growtogether.member.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//    private final PointService pointService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authentication);  // 인증 정보를 SecurityContext에 저장
        // 이후 추가 로직
//        String accessToken = jwtUtil.generateAccessToken(authentication);  // JwtUtil에서 토큰 발행
//        response.addHeader("Authorization", "Bearer " + accessToken);  // 헤더에 토큰 추가
        // 추가적인 후속 처리
    }

}