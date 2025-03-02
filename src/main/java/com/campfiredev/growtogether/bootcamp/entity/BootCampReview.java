package com.campfiredev.growtogether.bootcamp.entity;

import com.campfiredev.growtogether.bootcamp.type.ProgramCourse;
import com.campfiredev.growtogether.common.entity.BaseEntity;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="boot_camp_review")
public class BootCampReview extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bootCampId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private ProgramCourse programCourse;

    @Column(nullable = false)
    private Long viewCount;

    @Column(nullable = false)
    private Integer likeCount;

    @Column(nullable = false)
    private String bootCampName;

    @Column(nullable = false)
    private LocalDate bootCampStartDate;

    @Column(nullable = false)
    private LocalDate bootCampEndDate;

    @Column(nullable = false)
    private Integer learningLevel;

    @Column(nullable = false)
    private Integer assistantSatisfaction;

    @Column(nullable = false)
    private Integer programSatisfaction;

    @OneToMany(mappedBy = "bootCampReview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BootCampSkill> bootCampSkills;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id",nullable = false)
    private MemberEntity member;

    //후기 삭제시 댓글도 같이 삭제되도록
    @OneToMany(mappedBy = "bootCampReview", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<BootCampComment> comments;

    @OneToMany(mappedBy = "bootCampReview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLike> likes;

    public void increaseLikeCount(){
        this.likeCount++;
    }

    public void decreaseLikeCount(){
        this.likeCount--;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }
}