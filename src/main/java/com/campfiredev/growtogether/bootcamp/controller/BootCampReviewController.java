package com.campfiredev.growtogether.bootcamp.controller;

import com.campfiredev.growtogether.bootcamp.dto.BootCampReviewCreateDto;
import com.campfiredev.growtogether.bootcamp.dto.BootCampReviewResponseDto;
import com.campfiredev.growtogether.bootcamp.dto.BootCampReviewUpdateDto;
import com.campfiredev.growtogether.bootcamp.service.BootCampReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bootcamp")
@RequiredArgsConstructor
public class BootCampReviewController {

    private final BootCampReviewService reviewService;
    private final BootCampReviewService bootCampReviewService;

    /**
     * 부트캠프 리뷰 등록
     * @param request
     * @return 성공메세지
     */
    @PostMapping
    public ResponseEntity<BootCampReviewCreateDto> createReview(@Valid @RequestBody BootCampReviewCreateDto request) {

        return ResponseEntity.ok(reviewService.createReview(request));
    }

    /**
     * 부트캠프 리뷰 수정
     * @param bootCampId
     * @param request
     * @return
     */
    @PutMapping("/{bootCampId}")
    public ResponseEntity<BootCampReviewUpdateDto> updateReview(@PathVariable Long bootCampId,@Valid @RequestBody BootCampReviewUpdateDto request) {

        return ResponseEntity.ok(reviewService.updateReview(bootCampId , request));
    }

    /**
     * 부트캠프 리뷰 삭제
     * @param bootCampId
     * @return
     */
    @DeleteMapping("/{bootCampId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long bootCampId) {

        reviewService.deleteReview(bootCampId);

        return ResponseEntity.ok("부트캠프 리뷰 삭제가 완료되었습니다.");
    }

    /**
     * 부트캠프 조회
     * 최신순 및 인기순 조회
     * 한 페이지당 9개
     */
    @GetMapping
    public ResponseEntity<BootCampReviewResponseDto.PageResponse> getBootCampReviews(
            @RequestParam(defaultValue = "0") int page ,
            @RequestParam(defaultValue = "new") String sortType){

        return ResponseEntity.ok(bootCampReviewService.getBootCampReviews(page,sortType));
    }



    /**
     * 부트캠프 상세 조회
     */
    @GetMapping("/{bootCampId}")
    public ResponseEntity<BootCampReviewResponseDto.Response> getBootCampDetail(@PathVariable Long bootCampId) {

        BootCampReviewResponseDto.Response response = bootCampReviewService.getBootCampReviewDetail(bootCampId);

        return ResponseEntity.ok(response);
    }

    /**
     * 부트캠프 좋아요
     */
    @PostMapping("/{bootCampId}/like")
    public ResponseEntity<String> toggleLike(@PathVariable Long bootCampId , @RequestParam Long userId){

        bootCampReviewService.toggleLike(bootCampId,userId);

        return ResponseEntity.ok("좋아요 상태가 변경되었습니다.");
    }

    /**
     * 부트캠프 검색 기능
     */
}