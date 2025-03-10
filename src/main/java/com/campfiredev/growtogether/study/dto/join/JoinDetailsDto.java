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
public class JoinDetailsDto {

  private String nickName;

  private List<String> skills;

  private String content;

  public static JoinDetailsDto from(StudyMemberEntity studyMemberEntity, String content) {
    return JoinDetailsDto.builder()
        .nickName(studyMemberEntity.getMember().getNickName())
        .content(content)
        .skills(studyMemberEntity.getMember().getUserSkills().stream()
            .map(skill -> skill.getSkill().getSkillName())
            .collect(Collectors.toList()))
        .build();
  }

}
