package com.campfiredev.growtogether.skill.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="skill")
public class SkillEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id")
    private Long skillId;

    @Column(name = "skillname", nullable = false)
    private String skillName;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "skill_img_url")
    private String skillImgUrl;

}
