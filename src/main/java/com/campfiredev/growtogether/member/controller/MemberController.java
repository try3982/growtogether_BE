package com.campfiredev.growtogether.member.controller;

import com.campfiredev.growtogether.mail.service.EmailService;
import com.campfiredev.growtogether.member.dto.MemberDto;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;


    // 회원가입 API
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody MemberDto request) {
        MemberEntity member = memberService.register(request);
        return ResponseEntity.ok(Map.of("message", "회원가입이 완료되었습니다.", "userId", member.getUserId()));
    }


    //  회원 이메일 인증 api
    @PostMapping("/email/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String email, @RequestParam String code) {
        boolean isVerified = emailService.verifyCode(email, code);

        if (isVerified) {
            return ResponseEntity.ok(Map.of("message", "이메일 인증이 완료되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "인증 코드가 올바르지 않거나 만료되었습니다."));
        }
    }


}
