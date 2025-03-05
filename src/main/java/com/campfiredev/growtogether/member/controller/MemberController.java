package com.campfiredev.growtogether.member.controller;

import com.campfiredev.growtogether.member.dto.MemberLoginDto;
import com.campfiredev.growtogether.member.dto.MemberRegisterDto;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.service.MemberService;
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

}
