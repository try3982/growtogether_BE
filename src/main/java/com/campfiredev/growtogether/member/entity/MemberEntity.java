package com.campfiredev.growtogether.member.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "member", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "nick_name"),
        @UniqueConstraint(columnNames = "phone_number")
})
@Getter
@Builder
@NoArgsConstructor
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_Id")
    private Long memberId;

    // 카카오 로그인 회원
    @Column(name = "kakao_id", nullable = true, length = 30)
    private String kakaoId;

    @Column(name = "nick_name", nullable = false, length = 20)
    private String nickName;

    @Column(name = "phone_number", nullable = true, length = 20)
    private String phone;
    
    // 카카오 로그인 경우 null
    @Column(name = "password", nullable = true, length = 100)
    private String password;


    @Column(name = "email", nullable = false, length = 30)
    private String email;

    @Setter
    @Builder.Default
    private Integer points = 100;

    @Column(name = "github_url")
    private String githubUrl;

    @Setter
    @Column(name = "profile_image_key")
    private String profileImageKey;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 사용자 기술 매핑
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberSkillEntity> userSkills;

    @Builder
    public MemberEntity(Long memberId, String kakaoId, String nickName, String phone, String password, String email, Integer points, String githubUrl, String profileImageKey, LocalDateTime createdAt, LocalDateTime updatedAt, List<MemberSkillEntity> userSkills) {
        this.memberId =memberId;
        this.kakaoId = kakaoId;
        this.nickName = nickName;
        this.phone = phone;
        this.password = password;
        this.email = email;
        this.points = points;
        this.githubUrl = githubUrl;
        this.profileImageKey = profileImageKey;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userSkills = userSkills;
    }

}
