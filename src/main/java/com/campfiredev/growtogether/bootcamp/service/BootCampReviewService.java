package com.campfiredev.growtogether.bootcamp.service;

import com.campfiredev.growtogether.bootcamp.dto.BootCampReviewCreateDto;
import com.campfiredev.growtogether.bootcamp.dto.BootCampReviewResponseDto;
import com.campfiredev.growtogether.bootcamp.dto.BootCampReviewUpdateDto;
import com.campfiredev.growtogether.bootcamp.entity.BootCampReview;
import com.campfiredev.growtogether.bootcamp.entity.BootCampSkill;
import com.campfiredev.growtogether.bootcamp.entity.ReviewLike;
import com.campfiredev.growtogether.bootcamp.repository.BootCampReviewRepository;
import com.campfiredev.growtogether.bootcamp.repository.BootCampSkillRepository;
import com.campfiredev.growtogether.bootcamp.repository.ReviewLikeRepository;
import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorCode;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.repository.MemberRepository;
import com.campfiredev.growtogether.skill.entity.SkillEntity;
import com.campfiredev.growtogether.skill.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BootCampReviewService {

    private final BootCampReviewRepository bootCampReviewRepository;
    private final BootCampSkillRepository bootCampSkillRepository;
    private final MemberRepository memberRepository;
    private final SkillRepository skillRepository;

    private static final String CREATED_AT = "createdAt";
    private static final String LIKE_COUNT = "likeCount";
    private static final String NEW = "new";
    private final ReviewLikeRepository reviewLikeRepository;

    //후기 등록
    @Transactional
    public BootCampReviewCreateDto createReview(BootCampReviewCreateDto request) {

        MemberEntity member = memberRepository.findById(request.getUserId())
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        BootCampReview review = request.toEntity(member);
        bootCampReviewRepository.save(review);

        if(request.getSkillNames() != null && !request.getSkillNames().isEmpty()){
            List<SkillEntity> skills = skillRepository.findBySkillNameIn(request.getSkillNames());

            List<BootCampSkill> bootCampSkills = skills.stream()
                    .map(skill -> BootCampSkill.builder()
                            .bootCampReview(review)
                            .skill(skill)
                            .build())
                    .collect(Collectors.toList());
            review.setBootCampSkills(bootCampSkills);
            bootCampSkillRepository.saveAll(bootCampSkills);
        }
        return BootCampReviewCreateDto.fromEntity(review);
    }

    //후기 수정
    @Transactional
    public BootCampReviewUpdateDto updateReview(Long bootCampId,BootCampReviewUpdateDto request) {

        BootCampReview review = bootCampReviewRepository.findById(bootCampId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        // 자기 자신 이외에 사람은 수정을 할 수 없게 예외처리 예정

        request.updateEntity(review);
        bootCampReviewRepository.save(review);

        bootCampSkillRepository.deleteByBootCampReview(review);
        bootCampSkillRepository.flush();

        if(request.getSkillNames() != null && !request.getSkillNames().isEmpty()){
            List<SkillEntity> skills = skillRepository.findBySkillNameIn(request.getSkillNames());

            List<BootCampSkill> bootCampSkills = skills.stream()
                    .map(skill -> BootCampSkill.builder()
                            .bootCampReview(review)
                            .skill(skill)
                            .build())
                    .collect(Collectors.toList());

            bootCampSkillRepository.saveAll(bootCampSkills);
        }

        return BootCampReviewUpdateDto.fromEntity(review);
    }

    //후기 삭제
    @Transactional
    public void deleteReview(Long bootCampReviewId){
        BootCampReview review =  bootCampReviewRepository.findById(bootCampReviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        bootCampSkillRepository.deleteByBootCampReview(review);

        bootCampReviewRepository.delete(review);
    }

    //후기 조회
    public BootCampReviewResponseDto.PageResponse getBootCampReviews(int page, String sortType){

        Pageable pageable ;

        if(NEW.equalsIgnoreCase(sortType)){
            pageable = PageRequest.of(page,9, Sort.by(Sort.Order.desc(CREATED_AT)));
        } else {
            pageable = PageRequest.of(page,9, Sort.by(Sort.Order.desc(LIKE_COUNT)));
        }

        List<BootCampReview> reviews = bootCampReviewRepository.findAllWithSkills(pageable);

        //댓글 개수 추가 예정

        return BootCampReviewResponseDto.PageResponse.fromEntityPage(new PageImpl<>(reviews, pageable, reviews.size()));
    }

    //후기 상세 조회
    @Transactional
    public BootCampReviewResponseDto.Response getBootCampReviewDetail(Long bootCampId){

        BootCampReview bootCampReview = bootCampReviewRepository.findByIdWithSkills(bootCampId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        bootCampReview.increaseViewCount();

        return BootCampReviewResponseDto.Response.fromEntity(bootCampReview);
    }

    //게시글 좋아요
    @Transactional
    public void toggleLike(Long reviewId , Long userId) {
        MemberEntity member = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        BootCampReview review = bootCampReviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        boolean isAlerady = reviewLikeRepository.existsByBootCampReviewAndMember(review,member);

        if(isAlerady){
            reviewLikeRepository.deleteByBootCampReviewAndMember(review,member);
            review.decreaseLikeCount();
        } else {
            ReviewLike like = ReviewLike.builder()
                                        .bootCampReview(review)
                                        .member(member)
                                        .build();

            reviewLikeRepository.save(like);
            review.increaseLikeCount();
        }

        bootCampReviewRepository.save(review);
    }



}