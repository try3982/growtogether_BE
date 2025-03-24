package com.campfiredev.growtogether.member.dto;

import com.campfiredev.growtogether.study.dto.post.StudyDTO;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageLikesDto {
    private Long postId;  // 게시글 ID
    private String title; // 게시글 제목
    private String type;  // 게시글 유형 (스터디 / 부트캠프 리뷰)
    private Integer people; // 총 모집 할 인원
    private List<String> skillName; // 기술스택
    private String status; // 진행상태

    @Setter
    private List<StudyDTO> studyDTOS;
}
