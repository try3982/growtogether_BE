package com.campfiredev.growtogether.study.vote.dto;

import com.campfiredev.growtogether.study.vote.entity.VoteEntity;
import com.campfiredev.growtogether.study.vote.type.VoteType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteDto {

  private Long voteId;

  private String title;

  private VoteType voteType;

  public static VoteDto fromEntity(VoteEntity voteEntity) {
    return VoteDto.builder()
        .voteId(voteEntity.getId())
        .title(voteEntity.getTitle())
        .build();
  }

}
