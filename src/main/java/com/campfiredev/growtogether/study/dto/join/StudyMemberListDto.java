package com.campfiredev.growtogether.study.dto.join;

import com.campfiredev.growtogether.study.entity.join.StudyMemberEntity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyMemberListDto {

  private List<Info> pending;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Info {
    private Long userId;

    private String nickname;

    private Long studyMemberId;
  }

  public static StudyMemberListDto fromEntity(List<StudyMemberEntity> list){

    return new StudyMemberListDto(list.stream()
        .map(a -> Info.builder()
            .userId(a.getMember().getUserId())
            .nickname(a.getMember().getNickName())
            .studyMemberId(a.getId())
            .build())
        .collect(Collectors.toList()));
  }
}
