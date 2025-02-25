package com.campfiredev.growtogether.bootcamp.entity;

import com.campfiredev.growtogether.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="boot_camp_comment")
public class BootCampComment extends BaseEntity {

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


    //user 추가 예정


}
