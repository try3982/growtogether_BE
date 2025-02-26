package com.campfiredev.growtogether.study.dto;

import com.campfiredev.growtogether.study.entity.Study;
import com.campfiredev.growtogether.study.entity.StudyStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyDTO {
    private Long studyId;

    @NotBlank(message = "제목은 반드시 입력되어야 합니다.")
    private String title;

    @NotBlank(message = "게시글의 내용은 반드시 입력되어야 합니다.")
    private String description;

    private Long viewCount;

    @NotNull(message = "최대정원을 반드시 입력해야 합니다.")
    @Min(value = 2, message = "최소인원은 2명이상 이여야 합니다.")
    private Integer maxParticipant;

    @NotNull(message = "시작일자는 반드시 입력해야 합니다.")
    private Date studyStartDate;

    @NotNull(message = "종료일자는 반드시 입력해야 합니다.")
    private Date studyEndDate;

    private StudyStatus studyStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer participant;

    @NotBlank(message = "어떤 형식의 스터디인지 입력해주세요.")
    private String type;

    private Integer studyCount;

    @NotEmpty(message = "스터디 진행시 사용할 기술스택을 입력해 주세요.")
    private List<String> skillNames;

    public static StudyDTO fromEntity(Study study) {
        List<String> skillNames = study.getSkillStudies().stream()
                .map(skillStudy -> skillStudy.getSkill().getSkillName())
                .toList();

        return StudyDTO.builder()
                .studyId(study.getStudyId())
                .title(study.getTitle())
                .description(study.getDescription())
                .viewCount(study.getViewCount())
                .maxParticipant(study.getMaxParticipant())
                .studyStartDate(study.getStudyStartDate())
                .studyEndDate(study.getStudyEndDate())
                .studyStatus(study.getStudyStatus())
                .createdAt(study.getCreatedAt())
                .updatedAt(study.getUpdatedAt())
                .participant(study.getParticipant())
                .type(study.getType())
                .studyCount(study.getStudyCount())
                .skillNames(skillNames)
                .build();
    }

}

