package com.campfiredev.growtogether.member.controller;


import com.campfiredev.growtogether.bootcamp.dto.BootCampReviewResponseDto;
import com.campfiredev.growtogether.bootcamp.service.BootCampReviewService;
import com.campfiredev.growtogether.member.dto.CustomUserDetails;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.service.MemberService;
import com.campfiredev.growtogether.study.entity.Study;
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

    private final MemberService memberService;
    private final BootCampReviewService bootCampReviewService;


    //  마이페이지 기본 정보 조회 API
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberEntity> getMyProfile(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.getMemberProfile(memberId));
    }

    // 사용자가 참여 중이거나 모집한 스터디 목록 조회 API
    @GetMapping("/{memberId}/studies")
    public ResponseEntity<List<Study>> getMyStudies(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.getMyStudies(memberId));
    }

   // 사용자가 좋아요한 게시글 개수 조회 API
    @GetMapping("/{memberId}/liked")
    public ResponseEntity<Long> getLikedStudyCount(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.getLikedStudyCount(memberId));
    }
    //  사용자가 좋아요한 부트캠프 후기 게시글개수 조회 API
//    @GetMapping("/{memberId}/liked-posts")
//    public ResponseEntity<Long> getLikedPostCount(@PathVariable Long memberId) {
//        return ResponseEntity.ok(memberService.getLikedPostCount(memberId));
//
//    }

    /**
     * 내가 좋아요한 게시글 모음
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
