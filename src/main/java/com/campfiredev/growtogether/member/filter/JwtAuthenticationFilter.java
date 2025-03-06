package com.campfiredev.growtogether.member.filter;

import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.member.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.campfiredev.growtogether.exception.response.ErrorCode.NOT_VALID_TOKEN;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final String[] PUBLIC_URLS;
    private final JwtUtil jwtUtil;
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher(); // 경로 패턴 매칭을 위한 AntPathMatcher

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 URL이 퍼블릭 URL에 포함되는지 확인
        String requestUri = request.getRequestURI();
        for (String publicUrl : PUBLIC_URLS) {
            if (antPathMatcher.match(publicUrl, requestUri)) {
                // 퍼블릭 URL인 경우, 필터를 건너뛰고 다음 필터로 넘김
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 요청에서 JWT를 추출
        String jwtToken = jwtUtil.getTokenFromHeader(request);

        // 토큰이 없거나 유효하지 않으면 인증 실패 처리
        if (jwtToken == null || !jwtUtil.isTokenValid(jwtToken)) {
            // 401 Unauthorized 상태 코드 설정
            throw new CustomException(NOT_VALID_TOKEN);
        }

        // JWT 토큰에서 사용자 정보 추출하여 인증 처리
        String email = jwtUtil.getMemberEmailFromToken(jwtToken);

        // 사용자 인증 처리
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, jwtToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 요청을 필터 체인에 전달
        filterChain.doFilter(request, response);
    }

}