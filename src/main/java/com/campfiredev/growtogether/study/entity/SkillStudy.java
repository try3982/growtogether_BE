package com.campfiredev.growtogether.study.entity;


import com.campfiredev.growtogether.common.entity.BaseEntity;
import com.campfiredev.growtogether.skill.entity.SkillEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillStudy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "skill_id")
    private SkillEntity skill;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study;

}

