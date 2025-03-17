package com.campfiredev.growtogether.member.controller;

import com.campfiredev.growtogether.bootcamp.dto.BootCampReviewResponseDto;
import com.campfiredev.growtogether.bootcamp.service.BootCampReviewService;
import com.campfiredev.growtogether.member.dto.CustomUserDetails;
import com.campfiredev.growtogether.member.dto.MyPageBookmarksDto;
import com.campfiredev.growtogether.member.dto.MyPageInfoDto;
import com.campfiredev.growtogether.member.service.MemberService;
import com.campfiredev.growtogether.member.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {


    private final BootCampReviewService bootCampReviewService;
    private final MyPageService myPageService;
    private  final MemberService memberService;
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


    @Operation(summary = "마이페이지 프로필 및 좋아요한 게시글 조회",
            description = "닉네임, 프로필 이미지, 포인트, 깃허브 URL, 기술 스택 및 좋아요한 게시글(찜한 부트캠프 후기게시글 + 북마크한 스터디 게시글)을 조회합니다.")
    @GetMapping("/info")
    public ResponseEntity<MyPageInfoDto> getMyPageInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        MyPageInfoDto myPageInfo = myPageService.getMyPageInfo(customUserDetails.getMemberId());
        return ResponseEntity.ok(myPageInfo);
    }


    //마이 페이지 내가 참여중인 스터디 갯수 + 좋아요한 부트캠프 리뷰 게시판 갯수
    @Operation(summary = "내가 참여 중인 스터디 개수 및 좋아요한 게시글 개수 조회",
            description = "참여 중인 스터디 개수와 좋아요한 게시글(스터디 북마크 + 부트캠프 리뷰 좋아요) 개수를 조회합니다.")
    @GetMapping("/bookmarks")
    public ResponseEntity<MyPageBookmarksDto> getMyBookmarks(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        MyPageBookmarksDto bookmarks = myPageService.getMyBookmarks(customUserDetails.getMemberId());
        return ResponseEntity.ok(bookmarks);
    }




}
