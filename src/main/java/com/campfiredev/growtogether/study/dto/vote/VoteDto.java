package com.campfiredev.growtogether.study.dto.vote;

import com.campfiredev.growtogether.study.entity.vote.ChangeVoteEntity;
import com.campfiredev.growtogether.study.entity.vote.KickVoteEntity;
import com.campfiredev.growtogether.study.entity.vote.VoteEntity;
import com.campfiredev.growtogether.study.type.VoteType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VoteDto {

  private Long voteId;

  private String title;

  private VoteType voteType;

  public static VoteDto fromEntity(VoteEntity voteEntity) {
    return VoteDto.builder()
        .voteId(voteEntity.getId())
        .title(voteEntity.getTitle())
        .voteType(determineVoteType(voteEntity))
        .build();
  }

  private static VoteType determineVoteType(VoteEntity voteEntity) {
    if (voteEntity instanceof KickVoteEntity) {
      return VoteType.KICK;
    } else if (voteEntity instanceof ChangeVoteEntity) {
      return VoteType.CHANGE;
    } else {
      throw new IllegalArgumentException("Unknown vote type: " + voteEntity.getClass());
    }
  }
}
