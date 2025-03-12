package com.campfiredev.growtogether.study.entity;

import com.campfiredev.growtogether.member.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudyComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyCommentId;

    @Column(nullable = false)
    @Setter
    private String commentContent;

    @Column(nullable = false)
    private Long parentCommentId;

    @Column(nullable = false)
    private long studyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;
}