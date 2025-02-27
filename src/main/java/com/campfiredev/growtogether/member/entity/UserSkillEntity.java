package com.campfiredev.growtogether.member.entity;

import com.campfiredev.growtogether.skill.entity.SkillEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Entity
@Table(name = "user_skill", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "skill_id"}) // user_id와 skill_id 조합을 유니크하게 설정
})
@Getter
@NoArgsConstructor
public class UserSkillEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_skill_id")
    private Long userSkillId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private SkillEntity skill;

    public UserSkillEntity(MemberEntity member, SkillEntity skill) {
        this.member = member;
        this.skill = skill;
    }
}
