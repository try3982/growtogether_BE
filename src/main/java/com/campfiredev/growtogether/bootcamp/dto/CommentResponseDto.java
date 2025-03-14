package com.campfiredev.growtogether.bootcamp.dto;

import com.campfiredev.growtogether.bootcamp.entity.BootCampComment;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {

    private Long commentId;
    private String content;
    private Long userId;
    private String nickname;
    private Boolean isDeleted;
    private List<CommentResponseDto> childComments;

    public static CommentResponseDto fromEntity(BootCampComment comment) {
        return CommentResponseDto.builder()
                .commentId(comment.getBootCampCommentId())
                .content(comment.getCommentContent())
                .userId(comment.getMember().getMemberId())
                .nickname(comment.getMember().getNickName())
                .isDeleted(comment.getIsDeleted())
                .childComments(comment.getChildComments().stream()
                        .map(CommentResponseDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PageResponse {
        private List<CommentResponseDto> comments;
        private int totalPages;
        private int page;
        private int totalElements;
        private int size;

        public static PageResponse fromEntityPage(Page<BootCampComment> bootCampComments) {
            return PageResponse.builder()
                    .comments(bootCampComments.getContent().stream()
                            .map(CommentResponseDto::fromEntity)
                            .collect(Collectors.toList()))
                    .totalPages(bootCampComments.getTotalPages())
                    .page(bootCampComments.getNumber())
                    .totalElements((int)bootCampComments.getTotalElements())
                    .size(9)
                    .build();
        }
    }
}
