package com.campfiredev.growtogether.study.dto;

import com.campfiredev.growtogether.study.entity.Study;
import com.campfiredev.growtogether.study.entity.StudyStatus;
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
    private String title;
    private String description;
    private Long viewCount;
    private Integer maxParticipant;
    private Date studyStartDate;
    private Date studyEndDate;
    private StudyStatus studyStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer participant;
    private String type;
    private Integer studyCount;
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

