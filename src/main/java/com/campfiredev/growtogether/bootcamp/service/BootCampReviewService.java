package com.campfiredev.growtogether.bootcamp.service;

import com.campfiredev.growtogether.bootcamp.dto.BootCampReviewCreateDto;
import com.campfiredev.growtogether.bootcamp.dto.BootCampReviewResponseDto;
import com.campfiredev.growtogether.bootcamp.dto.BootCampReviewSearchRequest;
import com.campfiredev.growtogether.bootcamp.dto.BootCampReviewUpdateDto;
import com.campfiredev.growtogether.bootcamp.entity.BootCampReview;
import com.campfiredev.growtogether.bootcamp.entity.BootCampSkill;
import com.campfiredev.growtogether.bootcamp.entity.ReviewLike;
import com.campfiredev.growtogether.bootcamp.repository.BootCampReviewRepository;
import com.campfiredev.growtogether.bootcamp.repository.BootCampReviewRepositoryCustom;
import com.campfiredev.growtogether.bootcamp.repository.BootCampSkillRepository;
import com.campfiredev.growtogether.bootcamp.repository.ReviewLikeRepository;
import com.campfiredev.growtogether.bootcamp.strategy.WeightCalculateStrategy;
import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorCode;
import com.campfiredev.growtogether.member.dto.CustomUserDetails;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.repository.MemberRepository;
import com.campfiredev.growtogether.member.service.S3Service;
import com.campfiredev.growtogether.skill.entity.SkillEntity;
import com.campfiredev.growtogether.skill.repository.SkillRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BootCampReviewService {

    private final BootCampReviewRepository bootCampReviewRepository;
    private final BootCampSkillRepository bootCampSkillRepository;
    private final BootCampReviewRepositoryCustom bootCampReviewRepositoryCustom;
    private final MemberRepository memberRepository;
    private final SkillRepository skillRepository;
    private final S3Service s3Service;

    private static final String CREATED_AT = "createdAt";
    private static final String LIKE_COUNT = "likeCount";
    private static final String NEW = "new";
    private final ReviewLikeRepository reviewLikeRepository;

    private final List<WeightCalculateStrategy> strategies;
    private Map<String , WeightCalculateStrategy> strategyMap;

    @PostConstruct
    private void initializeStrategyMap() {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(strategy -> strategy.getClass().getSimpleName(), strategy -> strategy));
    }

    //후기 등록
    @Transactional
    public BootCampReviewCreateDto createReview(BootCampReviewCreateDto request , MultipartFile imageKey, CustomUserDetails customUserDetails) {

        MemberEntity member = memberRepository.findByEmail(customUserDetails.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        BootCampReview review = request.toEntity(member);

        if (imageKey != null && !imageKey.isEmpty()) {
            String imageUrl = s3Service.uploadFile(imageKey);
            review.setImageUrl(imageUrl);
        }

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
    public BootCampReviewUpdateDto updateReview(Long bootCampId, BootCampReviewUpdateDto request ,MultipartFile imageKey, CustomUserDetails customUserDetails) {

        BootCampReview review = bootCampReviewRepository.findById(bootCampId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        MemberEntity member = memberRepository.findByEmail(customUserDetails.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!review.getMember().equals(member)){
            throw new CustomException(ErrorCode.NOT_AUTHOR);
        }

        if (imageKey != null && !imageKey.isEmpty()) {
            if (review.getImageUrl() != null) {
                s3Service.deleteFile(review.getImageUrl());
            }
            String imageUrl = s3Service.uploadFile(imageKey);
            review.setImageUrl(imageUrl);
        }

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
    @Transactional
    public BootCampReviewResponseDto.PageResponse getBootCampReviews(int page, String sortType){

        Pageable pageable;

        if(NEW.equalsIgnoreCase(sortType)){
            pageable = PageRequest.of(page,9, Sort.by(Sort.Order.desc(CREATED_AT)));
        } else {
            pageable = PageRequest.of(page,9, Sort.by(Sort.Order.desc(LIKE_COUNT)));
        }

        List<BootCampReview> reviews = bootCampReviewRepository.findAllWithSkills(pageable);

        Map<Long, Integer> commentCounts = getCommentCounts(reviews);

        List<BootCampReview> updatedReviews = reviews.stream()
                .peek(review -> review.setCommentCount(commentCounts.getOrDefault(review.getBootCampId(), 0)))
                .toList();

        return BootCampReviewResponseDto.PageResponse.fromEntityPage(new PageImpl<>(updatedReviews, pageable, reviews.size()));
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
    public void toggleLike(Long reviewId , CustomUserDetails customUserDetails) {

        MemberEntity member = memberRepository.findByEmail(customUserDetails.getEmail())
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

    //검색 기능
    public BootCampReviewResponseDto.PageResponse searchBootCamps(BootCampReviewSearchRequest request){

        Sort sort;

        if(NEW.equalsIgnoreCase(request.getSortType())){
            sort = Sort.by(Sort.Order.desc(CREATED_AT));
        } else {
            sort = Sort.by(Sort.Order.desc(LIKE_COUNT));
        }

        Pageable pageable = PageRequest.of(request.getPage(),request.getSize(),sort);

        Page<BootCampReview> bootCampReviews = bootCampReviewRepositoryCustom.searchBootCamps(
                request.getBootCampName(),
                request.getTitle(),
                request.getProgramCourse(),
                request.getSkillName(),
                pageable
        );

        return BootCampReviewResponseDto.PageResponse.fromEntityPage(bootCampReviews);
    }
  //  @Transactional(readOnly = true)
    public List<BootCampReviewResponseDto.Response> getTopBootCampReviews(String strategyType , int limit){
        WeightCalculateStrategy strategy = strategyMap.getOrDefault(strategyType,strategyMap.get("WeightStrategy"));

        List<BootCampReview> topReviews = bootCampReviewRepositoryCustom.findTopRankedReviews(strategy,limit);

        return topReviews.stream()
                .map(review -> {
                    BootCampReviewResponseDto.Response reviewDto = BootCampReviewResponseDto.Response.fromEntity(review);
                    reviewDto.setCommentCount(review.getComments().size());
                    return reviewDto;
                })
                .collect(Collectors.toList());
    }

    public BootCampReviewResponseDto.PageResponse getLikeReviews(CustomUserDetails customUserDetails, Pageable pageable) {

        MemberEntity member = memberRepository.findByEmail(customUserDetails.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Page<ReviewLike> reviewLikes = reviewLikeRepository.findByMember(member, pageable);
        Page<BootCampReview> bootCampReviews = reviewLikes.map(ReviewLike::getBootCampReview);

        return BootCampReviewResponseDto.PageResponse.fromEntityPage(bootCampReviews);
    }


    public List<String> getProgramCourse(){

        return skillRepository.findDistinctCategories();
    }

    public List<String> getSkillName(){

        return skillRepository.findDistinctSkillName();
    }

    private Map<Long,Integer> getCommentCounts(List<BootCampReview> reviews){
        List<Long> bootCampIds = reviews.stream().map(BootCampReview::getBootCampId)
                .toList();

        return bootCampReviewRepository.findCommentCountsByBootCampIds(bootCampIds)
                .stream()
                .collect(Collectors.toMap(
                        result -> (Long) result[0],
                        result -> ((Number) result[1]).intValue()
                ));
    }
}