package com.campfiredev.growtogether.study.entity;

import com.campfiredev.growtogether.common.entity.BaseEntity;
import com.campfiredev.growtogether.study.dto.StudyDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Study extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyId;

    private String title;

    private String description;

    private Long viewCount;

    private Integer maxParticipant;

    private Date studyStartDate;

    private Date studyEndDate;

    private Boolean isDeleted;

    @Enumerated(EnumType.STRING)
    private StudyStatus studyStatus;

    private Integer participant;

    private String type;

    private Integer studyCount;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkillStudy> skillStudies;


    public static Study fromDTO(StudyDTO dto) {
        return Study.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .viewCount(0L)
                .maxParticipant(dto.getMaxParticipant())
                .studyStartDate(dto.getStudyStartDate())
                .studyEndDate(dto.getStudyEndDate())
                .isDeleted(false)
                .studyStatus(StudyStatus.PROGRESS)
                .participant(0)
                .type(dto.getType())
                .studyCount(0)
                .build();
    }

    public void addSkillStudies(List<SkillStudy> skillStudies) {
        this.skillStudies = skillStudies;
    }
}
