package com.campfiredev.growtogether.bootcamp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="review_like")
public class ReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long likeId;

    //좋아요 누른 사용자 필드 생성 예정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "boot_camp_review_id",nullable = false)
    private BootCampReview bootCampReview;
}
