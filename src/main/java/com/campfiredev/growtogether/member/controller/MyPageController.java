package com.campfiredev.growtogether.member.controller;

import com.campfiredev.growtogether.bootcamp.dto.BootCampReviewResponseDto;
import com.campfiredev.growtogether.bootcamp.service.BootCampReviewService;
import com.campfiredev.growtogether.member.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {


    private final BootCampReviewService bootCampReviewService;

    /**
     * 부트캠프 좋아요
     */
    @Operation(summary = "내가 좋아요한 부트캠프 리뷰 조회", description = "사용자가 좋아요한 부트캠프 리뷰 목록을 조회합니다.")
    @GetMapping("/myLikes")
    public ResponseEntity<BootCampReviewResponseDto.PageResponse> getLikedReviews(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(defaultValue = "0") int page ,
            @RequestParam(defaultValue = "9") int size) {

        Pageable pageable = PageRequest.of(page,size);

        BootCampReviewResponseDto.PageResponse likedReviews = bootCampReviewService.getLikeReviews(customUserDetails, pageable);
        return ResponseEntity.ok(likedReviews);
    }
}
