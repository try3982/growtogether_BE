package com.campfiredev.growtogether.bootcamp.controller;

import com.campfiredev.growtogether.bootcamp.dto.BootCampReviewCreateDto;
import com.campfiredev.growtogether.bootcamp.dto.BootCampReviewResponseDto;
import com.campfiredev.growtogether.bootcamp.dto.BootCampReviewSearchRequest;
import com.campfiredev.growtogether.bootcamp.dto.BootCampReviewUpdateDto;
import com.campfiredev.growtogether.bootcamp.service.BootCampReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/bootcamp")
@RequiredArgsConstructor
public class BootCampReviewController {

    private final BootCampReviewService bootCampReviewService;

    /**
     * 부트캠프 리뷰 등록
     * @param request
     * @return 성공메세지
     */
    @PostMapping
    public ResponseEntity<BootCampReviewCreateDto> createReview(@Valid @RequestPart("bootCampReview") BootCampReviewCreateDto request,
                                                                @RequestPart(value = "imageUrl",required = false) MultipartFile imageKey, Authentication authentication) {

        return ResponseEntity.ok(bootCampReviewService.createReview(request,imageKey,authentication));
    }

    /**
     * 부트캠프 리뷰 수정
     * @param bootCampId
     * @param request
     * @return
     */
    @PutMapping("/{bootCampId}")
    public ResponseEntity<BootCampReviewUpdateDto> updateReview(@PathVariable Long bootCampId, @Valid @RequestPart("bootCampReview") BootCampReviewUpdateDto request , @RequestPart(value = "imageUrl",required = false) MultipartFile imageKey,Authentication authentication) {

        return ResponseEntity.ok(bootCampReviewService.updateReview(bootCampId ,request,imageKey,authentication));
    }

    /**
     * 부트캠프 리뷰 삭제
     * @param bootCampId
     * @return
     */
    @DeleteMapping("/{bootCampId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long bootCampId) {

        bootCampReviewService.deleteReview(bootCampId);

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
    public ResponseEntity<String> toggleLike(@PathVariable Long bootCampId , Authentication authentication){

        bootCampReviewService.toggleLike(bootCampId,authentication);

        return ResponseEntity.ok("좋아요 상태가 변경되었습니다.");
    }

    /**
     * 부트캠프 검색 기능
     */
    @GetMapping("/search")
    public ResponseEntity<BootCampReviewResponseDto.PageResponse> searchBootCamps(
            @ModelAttribute BootCampReviewSearchRequest request){

        BootCampReviewResponseDto.PageResponse res = bootCampReviewService.searchBootCamps(request);

        return ResponseEntity.ok(res);
    }

    /**
     * 인기 게시글
     */
    @GetMapping("/top")
    public List<BootCampReviewResponseDto.Response> getTopReviews(
            @RequestParam(defaultValue = "WeightStrategy") String strategyType,
            @RequestParam(defaultValue = "5") int limit) {

        return bootCampReviewService.getTopBootCampReviews(strategyType, limit);
    }

    /**
     * 내가 좋아요한 게시글 모음
     */
    @GetMapping("/myLikes")
    public ResponseEntity<BootCampReviewResponseDto.PageResponse> getLikedReviews(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page ,
            @RequestParam(defaultValue = "9") int size) {

        Pageable pageable = PageRequest.of(page,size);

        BootCampReviewResponseDto.PageResponse likedReviews = bootCampReviewService.getLikeReviews(authentication, pageable);
        return ResponseEntity.ok(likedReviews);
    }

    @GetMapping("/programCourses")
    public List<String> getProgramCourse(){

        return bootCampReviewService.getProgramCourse();
    }

    @GetMapping("/skillName")
    public List<String> getSkillName(){

        return bootCampReviewService.getSkillName();
    }
}