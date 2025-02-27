package com.campfiredev.growtogether.member.controller;

import com.campfiredev.growtogether.mail.service.EmailService;
import com.campfiredev.growtogether.member.dto.MemberDto;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.service.MemberService;
import com.campfiredev.growtogether.member.service.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final S3Service s3Service;
    private final EmailService emailService;

    // 회원가입 API
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestPart("data") MemberDto request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        MemberEntity member = memberService.register(request, profileImage);
        return ResponseEntity.ok(Map.of("message", "회원가입이 완료되었습니다.", "userId", member.getUserId()));
    }




}
