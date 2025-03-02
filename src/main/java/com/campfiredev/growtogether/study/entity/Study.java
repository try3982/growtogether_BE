package com.campfiredev.growtogether.study.entity;

import com.campfiredev.growtogether.common.entity.BaseEntity;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.study.dto.StudyDTO;
import jakarta.persistence.*;
import lombok.*;

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

    @Setter
    private Boolean isDeleted;

    @Enumerated(EnumType.STRING)
    private StudyStatus studyStatus;

    private Integer participant;

    private String type;

    private Integer studyCount;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    private List<SkillStudy> skillStudies;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private MemberEntity member;

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
                .participant(1)
                .type(dto.getType())
                .studyCount(0)
                .build();
    }

    public void addSkillStudies(List<SkillStudy> skillStudies) {
        this.skillStudies = skillStudies;
    }

    public void setAuthor(MemberEntity author) {
        this.member = author;
    }

    public void updateViewCount() {
        this.viewCount++;
    }
    public void updateFromDto(StudyDTO dto, List<SkillStudy> newSkillStudies) {
        this.title = dto.getTitle();
        this.description = dto.getDescription();
        this.maxParticipant = dto.getMaxParticipant();
        this.studyStartDate = dto.getStudyStartDate();
        this.studyEndDate = dto.getStudyEndDate();
        this.type = dto.getType();
        this.skillStudies.addAll(newSkillStudies);
    }


}
