package com.campfiredev.growtogether.bootcamp.dto;

import com.campfiredev.growtogether.bootcamp.entity.BootCampReview;
import com.campfiredev.growtogether.bootcamp.type.ProgramCourse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BootCampReviewUpdateDto {


    @NotBlank(message = "제목은 필수 입력값입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력값입니다.")
    private String content;

    @NotBlank(message = "부트캠프 이름은 필수 입력값입니다.")
    private String bootCampName;

    @NotNull(message = "학습난이도는 필수 입력값입니다.")
    private Integer learningLevel;

    @NotNull(message = "취업 지원 만족도는 필수 입력값입니다.")
    private Integer assistantSatisfaction;

    @NotNull(message = "강의 만족도는 필수 입력값입니다.")
    private Integer programSatisfaction;

    @NotNull(message = "프로그램 과정은 필수 입력값 입니다.")
    private ProgramCourse programCourse;

    @NotNull(message = "부트캠프 시작 날짜는 필수 입력값입니다.")
    private LocalDate bootCampStartDate;

    @NotNull(message = "부트캠프 종료날짜는 필수 입력값입니다.")
    private LocalDate bootCampEndDate;

    private List<String> skillNames;

    public void updateEntity(BootCampReview review) {
        review.setTitle(title);
        review.setContent(content);
        review.setBootCampName(bootCampName);
        review.setLearningLevel(learningLevel);
        review.setAssistantSatisfaction(assistantSatisfaction);
        review.setProgramSatisfaction(programSatisfaction);
        review.setProgramCourse(programCourse);
        review.setBootCampStartDate(bootCampStartDate);
        review.setBootCampEndDate(bootCampEndDate);
    }

    public static BootCampReviewUpdateDto fromEntity(BootCampReview review){

        List<String> skillNames = review.getBootCampSkills().stream()
                .map(skill -> skill.getSkill().getSkillName())
                .toList();

        return BootCampReviewUpdateDto.builder()
                .title(review.getTitle())
                .content(review.getContent())
                .bootCampName(review.getBootCampName())
                .learningLevel(review.getLearningLevel())
                .assistantSatisfaction(review.getAssistantSatisfaction())
                .programSatisfaction(review.getProgramSatisfaction())
                .programCourse(review.getProgramCourse())
                .bootCampStartDate(review.getBootCampStartDate())
                .bootCampEndDate(review.getBootCampEndDate())
                .skillNames(skillNames)
                .build();
    }

}