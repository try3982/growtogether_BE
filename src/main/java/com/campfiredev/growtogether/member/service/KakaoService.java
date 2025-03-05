package com.campfiredev.growtogether.member.service;

import com.campfiredev.growtogether.common.config.ProviderConfig;
import com.campfiredev.growtogether.common.config.RegistrationConfig;
import com.campfiredev.growtogether.member.dto.KakaoTokenDto;
import com.campfiredev.growtogether.member.dto.KakaoUserDto;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaoService {

    private final RestTemplate restTemplate;
    private final MemberService memberService;

    private final RegistrationConfig registrationConfig;
    private final ProviderConfig providerConfig;

    private final JwtUtil jwtUtil;

    @Transactional
    public String getAccessToken(String accessCode) {
        KakaoTokenDto kakaoToken = getKakaoToken(accessCode);
        KakaoUserDto kakaoUserInfo = getKakaoUserInfo(kakaoToken.getAccessToken());
        MemberEntity memberEntity = memberService.kakaoLogin(kakaoUserInfo);
        return jwtUtil.generateAccessToken(memberEntity.getMemberId());
    }

    // 카카오 Token 요청
    public KakaoTokenDto getKakaoToken(String accessCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", registrationConfig.clientId());
        params.add("redirect_url", registrationConfig.redirectUri());
        params.add("code", accessCode);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<KakaoTokenDto> response = restTemplate.exchange(
                providerConfig.tokenUri(),
                HttpMethod.POST,
                kakaoTokenRequest,
                KakaoTokenDto.class);

        return response.getBody();
    }

    // 카카오 사용자 정보 가져오기
    public KakaoUserDto getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserDto> response = restTemplate.exchange(
                providerConfig.userInfoUri(),
                HttpMethod.GET,
                entity,
                KakaoUserDto.class);

        return response.getBody();
    }

}
