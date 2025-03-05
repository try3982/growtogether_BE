package com.campfiredev.growtogether.member.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.access-token.expiration-time}")
    private long expirationTime;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 액세스 토큰을 발급하는 메서드
    public String generateAccessToken(Long memberId) {
        log.info("액세스 토큰이 발행되었습니다.");

        return Jwts.builder()
                   .claim("memberId",memberId.toString()) // 클레임에memberId 추가
                   .issuedAt(new Date())
                   .expiration(new Date(System.currentTimeMillis() + expirationTime))
                   .signWith(this.getSigningKey())
                   .compact();
    }

    // 리프레쉬 토큰을 발급하는 메서드
    public String generateRefreshToken(Long memberId) {
        log.info("리프레쉬 토큰이 발행되었습니다.");

        return Jwts.builder()
                   .claim("memberId",memberId.toString()) // 클레임에memberId 추가
                   .issuedAt(new Date())
                   .expiration(new Date(System.currentTimeMillis() + expirationTime))
                   .signWith(this.getSigningKey())
                   .compact();
    }


    // 응답 헤더에서 액세스 토큰을 반환하는 메서드
    public String getTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 부분 제거하고 토큰만 추출
        }
        return null;
    }

    // 토큰에서 유저 id를 반환하는 메서드
    public String getMemberIdFromToken(String token) {
        try {
            String memberId = Jwts.parser()
                                .verifyWith(this.getSigningKey())
                                .build()
                                .parseSignedClaims(token)
                                .getPayload()
                                .get("memberId", String.class);
            log.info("유저 id를 반환합니다.");
            return memberId;
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰이 유효하지 않은 경우
            log.warn("유효하지 않은 토큰입니다.");
            throw new RuntimeException();
        }
    }

    // Jwt 토큰의 유효기간을 확인하는 메서드
    public boolean isTokenValid(String token) {
        try {
            Date expirationDate = Jwts.parser()
                                      .verifyWith(this.getSigningKey())
                                      .build()
                                      .parseSignedClaims(token)
                                      .getPayload()
                                      .getExpiration();
            log.info("토큰의 유효기간을 확인합니다.");
            return expirationDate.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰이 유효하지 않은 경우
            log.warn("유효하지 않은 토큰입니다.");
            throw new RuntimeException();
        }
    }
}