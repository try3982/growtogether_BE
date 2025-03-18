package com.campfiredev.growtogether.bootcamp.entity;

import com.campfiredev.growtogether.common.entity.BaseEntity;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<BootCampComment> childComments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boot_camp_id",nullable = false)
    private BootCampReview bootCampReview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id",nullable = false)
    private MemberEntity member;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @Column(nullable = false)
    private int depth;
}
