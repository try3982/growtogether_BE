package com.campfiredev.growtogether.study.entity;

import com.campfiredev.growtogether.study.dto.StudyCommentDto;
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

    public static StudyComment fromDto(StudyCommentDto dto) {
        return StudyComment.builder()
                .commentContent(dto.getCommentContent())
                .parentCommentId(dto.getParentCommentId())
                .build();
    }
}