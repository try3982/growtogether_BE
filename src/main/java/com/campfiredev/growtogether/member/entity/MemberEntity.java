package com.campfiredev.growtogether.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "'user'", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "nick_name"),
        @UniqueConstraint(columnNames = "phone_number")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "nick_name", nullable = false, length = 20)
    private String nickName;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phone;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "email", nullable = false, length = 30)
    private String email;

    @Builder.Default
    private Integer points = 100;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "profile_image_key")
    private String profileImageKey;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 사용자 기술 매핑
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSkillEntity> userSkills;

    public void setProfileImageKey(String profileImageKey) {
        this.profileImageKey = profileImageKey;
    }
}
