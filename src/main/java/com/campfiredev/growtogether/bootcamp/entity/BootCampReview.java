package com.campfiredev.growtogether.bootcamp.entity;

import com.campfiredev.growtogether.bootcamp.type.ProgramCourse;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="boot_camp_review")
public class BootCampReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="boot_camp_review_id")
    private Long bootCampReviewId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private ProgramCourse programCourse;

    @Column(nullable = false)
    private Long viewCount;

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
    private Integer likeCount;

    //user 추가 예정

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
