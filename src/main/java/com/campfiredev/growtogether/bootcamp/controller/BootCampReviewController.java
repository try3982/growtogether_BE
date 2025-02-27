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
    public ResponseEntity<?> createReview(@Valid @RequestBody BootCampReviewCreateDto.Request request) {


        reviewService.createReview(request);

        return ResponseEntity.ok("부트캠프 리뷰 등록이 완료되었습니다.");
    }

    /**
     * 부트캠프 리뷰 수정
     * @param bootCampReviewId
     * @param request
     * @return
     */
    @PutMapping("/{bootCampReviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long bootCampReviewId,@Valid @RequestBody BootCampReviewUpdateDto.Request request) {


        reviewService.updateReview(bootCampReviewId , request);

        return ResponseEntity.ok("부트캠프 리뷰 수정이 완료되었습니다.");
    }

    /**
     * 부트캠프 리뷰 삭제
     * @param bootCampReviewId
     * @return
     */
    @DeleteMapping("/{bootCampReviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long bootCampReviewId) {

        reviewService.deleteReview(bootCampReviewId);

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


    /**
     * 부트캠프 검색 기능
     */
}