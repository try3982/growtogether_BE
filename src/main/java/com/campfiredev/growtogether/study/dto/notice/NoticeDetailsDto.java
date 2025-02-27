package com.campfiredev.growtogether.study.dto.notice;

import com.campfiredev.growtogether.study.entity.notice.NoticeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeDetailsDto {

  private Long id;

  private String title;

  private String content;

  //BaseEntity 추가 후 사용
  //private LocalDateTime createdAt;

  public static NoticeDetailsDto fromEntity(NoticeEntity noticeEntity) {
    return NoticeDetailsDto.builder()
        .id(noticeEntity.getId())
        .title(noticeEntity.getTitle())
        .content(noticeEntity.getContent())
        //.createdAt(noticeEntity.getCreatedAt) // 추후 추가
        .build();
  }

}
