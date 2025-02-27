package com.campfiredev.growtogether.skill.entity;

import com.campfiredev.growtogether.study.entity.SkillStudy;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long skillId;

    @Column(nullable = false)
    private String skillName;

    @Column(nullable = false)
    private String category;

    private String skillImgUrl;

    @OneToMany(mappedBy = "skill")
    private List<SkillStudy> skillStudies;
}
