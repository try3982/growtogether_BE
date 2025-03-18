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
	public  ResponseEntity<?> kakaoLogin(
			@RequestParam("code") String accessCode,
			HttpServletResponse httpServletResponse
	) {
		String accessToken = kakaoService.getAccessToken(accessCode);

		httpServletResponse.setHeader("Authorization", "Bearer " + accessToken);
		httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");

		//	Map<String, String> response = Map.of("accessToken", accessToken);
		//	return "redirect:/http://13.125.21.225:8080/oauth/kakao/";

//		return "redirect:/oauth/kakao";
//        httpServletResponse.sendRedirect("http://localhost:8080/oauth/kakao");
		return ResponseEntity.ok(Map.of("message", "로그인이 완료되었습니다.", "accessToken", accessToken));
    }

	@GetMapping("/oauth/kakao")
	public String a(){
		return "a";
	}
}