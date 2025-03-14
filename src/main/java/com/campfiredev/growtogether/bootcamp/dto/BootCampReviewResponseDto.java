package com.campfiredev.growtogether.bootcamp.dto;

import com.campfiredev.growtogether.bootcamp.entity.BootCampReview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class BootCampReviewResponseDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String title;
        private String author;
        private String profileImageUrl;
        private String content;
        private String imageUrl;
        private String bootCampName;
        private LocalDate startdate;
        private LocalDate enddate;
        private int learningLevel;
        private int assistantSatisfaction;
        private int programSatisfaction;
        private int likeCount;
        private Long viewCount;
        private int commentCount;
        private List<String> skillNames;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response fromEntity(BootCampReview review) {

            List<String> skillNames = review.getBootCampSkills().stream()
                    .map(skill -> skill.getSkill().getSkillName())
                    .toList();

            return Response.builder()
                    .id(review.getBootCampId())
                    .title(review.getTitle())
                    .author(review.getMember().getNickName())
                    .profileImageUrl(review.getMember().getProfileImageUrl())
                    .content(review.getContent())
                    .imageUrl(review.getImageUrl())
                    .bootCampName(review.getBootCampName())
                    .startdate(review.getBootCampStartDate())
                    .enddate(review.getBootCampEndDate())
                    .assistantSatisfaction(review.getAssistantSatisfaction())
                    .programSatisfaction(review.getProgramSatisfaction())
                    .likeCount(review.getLikeCount())
                    .viewCount(review.getViewCount())
                    .commentCount(review.getComments().size())
                    .skillNames(skillNames)
                    .createdAt(review.getCreatedAt())
                    .updatedAt(review.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PageResponse {
        private List<Response> reviews;
        private int totalPages;
        private int page;
        private int totalElements;
        private int size;

        public static PageResponse fromEntityPage(Page<BootCampReview> bootCampReviews){

            return PageResponse.builder()
                    .reviews(bootCampReviews.getContent().stream()
                            .map(Response::fromEntity)
                            .collect(Collectors.toList()))
                    .totalPages(bootCampReviews.getTotalPages())
                    .page(bootCampReviews.getNumber())
                    .totalElements(bootCampReviews.getNumberOfElements())
                    .size(9)
                    .build();
        }
    }
}