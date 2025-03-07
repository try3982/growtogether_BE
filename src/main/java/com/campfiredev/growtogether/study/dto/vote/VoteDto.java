package com.campfiredev.growtogether.study.dto.vote;

import com.campfiredev.growtogether.study.entity.vote.VoteEntity;
import com.campfiredev.growtogether.study.type.VoteType;
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
