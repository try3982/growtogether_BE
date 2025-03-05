package com.campfiredev.growtogether.member.filter;

import com.campfiredev.growtogether.member.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String jwtToken = jwtUtil.getTokenFromHeader(request); // 요청에서 JWT를 추출
//        if (jwtToken != null && jwtUtil.isTokenValid(jwtToken)) {  // JWT 검증
//            // JWT 토큰에서 사용자 정보 추출하여 인증 처리
//            String userName = jwtUtil.getMemberIdFromToken(jwtToken);
//            // 사용자 인증 처리
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName, jwtToken);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
        filterChain.doFilter(request, response); // 요청을 필터 체인에 전달
    }

}
