package com.campfiredev.growtogether.study.dto.join;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyMemberInfoDto {

  private String nickName;
  private Long studyMemberId;
}
