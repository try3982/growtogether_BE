package com.campfiredev.growtogether.bootcamp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="boot_camp_comment")
public class BootCampComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bootCampCommentId;

    @Column(nullable = false)
    private String commentContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private BootCampComment parentComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boot_camp_review_id",nullable = false)
    private BootCampReview bootCampReview;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //user 추가 예정

    @PrePersist
    public void onPrePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onPreUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
