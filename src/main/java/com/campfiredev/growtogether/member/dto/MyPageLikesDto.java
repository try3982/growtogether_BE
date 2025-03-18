package com.campfiredev.growtogether.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageLikesDto {
    private Long postId;  // 게시글 ID
    private String title; // 게시글 제목
    private String type;  // 게시글 유형 (스터디 / 부트캠프 리뷰)
}
