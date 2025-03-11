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
  private String nickname;

  private Long studyMemberId;

  public static StudyMemberListDto fromEntity(StudyMemberEntity studyMemberEntity){

    return StudyMemberListDto.builder()
        .nickname(studyMemberEntity.getMember().getNickName())
        .studyMemberId(studyMemberEntity.getId())
        .build();
  }
}

