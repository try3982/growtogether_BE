package com.campfiredev.growtogether.member.controller;

import com.campfiredev.growtogether.member.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/member/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        String fileUrl = s3Service.uploadFile(file);
        return ResponseEntity.ok(Map.of("message", "프로필 이미지 업로드 성공", "imageUrl", fileUrl));
    }
    // 프로필 이미지 URL 조회 API
    @GetMapping("/image/{fileKey}")
    public ResponseEntity<?> getProfileImageUrl(@PathVariable String fileKey) {
        String fileUrl = s3Service.getFileUrl(fileKey);
        return ResponseEntity.ok(Map.of("profileImageUrl", fileUrl));
    }
}
