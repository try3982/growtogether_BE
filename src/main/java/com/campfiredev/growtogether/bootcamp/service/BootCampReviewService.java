package com.campfiredev.growtogether.bootcamp.service;

import com.campfiredev.growtogether.bootcamp.dto.BootCampReviewCreateDto;
import com.campfiredev.growtogether.bootcamp.dto.BootCampReviewResponseDto;
import com.campfiredev.growtogether.bootcamp.dto.BootCampReviewUpdateDto;
import com.campfiredev.growtogether.bootcamp.entity.BootCampReview;
import com.campfiredev.growtogether.bootcamp.entity.BootCampSkill;
import com.campfiredev.growtogether.bootcamp.repository.BootCampReviewRepository;
import com.campfiredev.growtogether.bootcamp.repository.BootCampSkillRepository;
import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorCode;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.repository.MemberRepository;
import com.campfiredev.growtogether.skill.entity.SkillEntity;
import com.campfiredev.growtogether.skill.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    //후기 등록
    @Transactional
    public void createReview(BootCampReviewCreateDto.Request request) {

        MemberEntity member = memberRepository.findByUserId(request.getUserId())
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

            bootCampSkillRepository.saveAll(bootCampSkills);
        }


    }

    //후기 수정
    @Transactional
    public void updateReview(Long bootCampReviewId,BootCampReviewUpdateDto.Request request) {

        BootCampReview review = bootCampReviewRepository.findById(bootCampReviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        // 자기 자신 이외에 사람은 수정을 할 수 없게 예외처리 예정

        request.updateEntity(review);

        bootCampReviewRepository.save(review);

    }

    //후기 삭제
    @Transactional
    public void deleteReview(Long bootCampReviewId){
        BootCampReview review =  bootCampReviewRepository.findById(bootCampReviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        bootCampReviewRepository.delete(review);
    }

    //후기 조회
    @Transactional(readOnly = true)
    public BootCampReviewResponseDto.PageResponse getBootCampReviews(int page, String sortType){

        Pageable pageable ;

        if("new".equalsIgnoreCase(sortType)){
            pageable = PageRequest.of(page,9, Sort.by(Sort.Order.desc("createdAt")));
        } else {
            pageable = PageRequest.of(page,9, Sort.by(Sort.Order.desc("likeCount")));
        }

        Page<BootCampReview> bootCampReviews = bootCampReviewRepository.findAll(pageable);

        //댓글 개수 , 스킬명들 추가 예정

        return BootCampReviewResponseDto.PageResponse.fromEntityPage(bootCampReviews);
    }

    //후기 상세 조회


}