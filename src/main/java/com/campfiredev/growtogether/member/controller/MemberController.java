package com.campfiredev.growtogether.member.controller;

import com.campfiredev.growtogether.member.dto.MemberDto;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    // 회원가입 API
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody MemberDto request) {
        MemberEntity member = memberService.register(request);
        return ResponseEntity.ok(Map.of("message", "회원가입이 완료되었습니다.", "userId", member.getUserId()));
    }

}
