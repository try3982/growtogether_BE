package com.campfiredev.growtogether.study.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillStudy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Setter
    @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study;

    public void linkStudy(Study study) {
        this.study = study;
    }
}

