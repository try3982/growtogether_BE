package com.campfiredev.growtogether.study.dto;

import com.campfiredev.growtogether.study.entity.StudyComment;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyCommentDto {

    private Long studyCommentId;

    @NotBlank(message = "댓글 내용은 반드시 입력되어야 합니다.")
    private String commentContent;

    @NotNull(message = "어떤 항목의 댓글인지 입력되어야 합니다.")
    private Long parentCommentId;

    @NotNull(message = "게시글의 번호가 반드시 입력되어야 합니다.")
    @Min(value = 0, message = "게시글 번호는 0보다 큰 숫자여야 합니다.")
    private Long studyId;

    private String nickName;


    public static StudyCommentDto fromEntity(StudyComment comment) {
        return StudyCommentDto.builder()
                .studyCommentId(comment.getStudyCommentId())
                .commentContent(comment.getCommentContent())
                .parentCommentId(comment.getParentCommentId())
                .studyId((comment.getStudyId()))
                .nickName(comment.getMember().getNickName())
                .build();
    }
}

