package com.campfiredev.growtogether.member.controller;

import com.campfiredev.growtogether.member.service.KakaoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class OAuthController {

	private final KakaoService kakaoService;

	@GetMapping("/oauth2/code/kakao")
	public ResponseEntity<Map<String, String>> kakaoLogin(
			@RequestParam("code") String accessCode,
			HttpServletResponse httpServletResponse
	) {
		String accessToken = kakaoService.getAccessToken(accessCode);

		httpServletResponse.setHeader("Authorization", "Bearer " + accessToken);

		Map<String, String> response = Map.of("accessToken", accessToken);
		return ResponseEntity.ok(response);
	}

}
