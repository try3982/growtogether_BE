package com.campfiredev.growtogether.study.dto.notice;

import com.campfiredev.growtogether.study.entity.notice.NoticeEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeListDto {
  private Page<Summary> notices;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Summary {
    private Long noticeId;

    private String title;

    private LocalDateTime createdAt;
  }

  public static NoticeListDto fromEntityPage(Page<NoticeEntity> noticePage) {
    Page<Summary> summaryPage = noticePage.map(notice ->
        Summary.builder()
            .noticeId(notice.getId())
            .title(notice.getTitle())
            .createdAt(notice.getCreatedAt())
            .build()
    );
    return new NoticeListDto(summaryPage);
  }
}
