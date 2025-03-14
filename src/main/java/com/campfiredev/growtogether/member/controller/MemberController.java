package com.campfiredev.growtogether.member.controller;

import com.campfiredev.growtogether.member.dto.CustomUserDetails;
import com.campfiredev.growtogether.member.dto.MemberLoginDto;
import com.campfiredev.growtogether.member.dto.MemberRegisterDto;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원가입 API
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody MemberRegisterDto memberRegisterDto,
            @RequestParam(required = false) MultipartFile profileImage) {
        MemberEntity user = memberService.register(memberRegisterDto, profileImage);
        return ResponseEntity.ok(Map.of("message", "회원가입이 완료되었습니다.", "user", user));
    }

   // 회원 로그인 api
    @PostMapping("/memberLogin")
    public ResponseEntity<?> userLogin(@RequestBody MemberLoginDto memberLoginDto) {
        String accessToken = memberService.userLogin(memberLoginDto);
        return ResponseEntity.ok(Map.of("message", "로그인이 완료되었습니다.", "accessToken", accessToken));
    }
    // 비밀번호 재설정 요청 (이메일 전송)
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        memberService.sendPasswordResetEmail(email);
        return ResponseEntity.ok(Map.of("message", "비밀번호 재설정 이메일이 전송되었습니다."));
    }
    // 비밀번호 재설정 처리
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        memberService.resetPassword(token, newPassword);
        return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));
    }
    // 이메일 찾기 API (사용자 요청 시 마스킹된 이메일 반환)
    @PostMapping("/find-email")
    public ResponseEntity<?> findEmail(@RequestParam String email) {
        String maskedEmail = memberService.findEmail(email);
        return ResponseEntity.ok(Map.of("maskedEmail", maskedEmail));
    }


}
