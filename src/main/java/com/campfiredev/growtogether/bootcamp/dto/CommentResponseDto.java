package com.campfiredev.growtogether.bootcamp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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

}
