package com.campfiredev.growtogether.member.controller;

import com.campfiredev.growtogether.member.service.KakaoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OAuthController {

	private final KakaoService kakaoService;

	@GetMapping("/oauth2/code/kakao")
	public void kakaoLogin(
			@RequestParam("code") String accessCode,
			HttpServletResponse httpServletResponse
	) {
		String accessToken = kakaoService.getAccessToken(accessCode);

		httpServletResponse.setHeader("Authorization", "Bearer " + accessToken);

		//	Map<String, String> response = Map.of("accessToken", accessToken);
		//	return "redirect:/http://13.125.21.225:8080/oauth/kakao/";

//		return "redirect:/oauth/kakao";
//        httpServletResponse.sendRedirect("http://localhost:8080/oauth/kakao");
    }

	@GetMapping("/oauth/kakao")
	public String a(){
		return "a";
	}
}