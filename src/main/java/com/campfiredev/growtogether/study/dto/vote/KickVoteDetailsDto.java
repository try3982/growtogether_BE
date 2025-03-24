package com.campfiredev.growtogether.study.dto.vote;

import com.campfiredev.growtogether.study.entity.vote.KickVoteEntity;
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
public class KickVoteDetailsDto extends VoteDto {

  private String nickName;

  private String profileImageUrl;

  public static KickVoteDetailsDto fromEntity(KickVoteEntity kickVoteEntity) {
    return KickVoteDetailsDto.builder()
        .voteId(kickVoteEntity.getId())
        .title(kickVoteEntity.getTitle())
        .voteType(VoteType.KICK)
        .nickName(kickVoteEntity.getStudyMember().getMember().getNickName())
        .profileImageUrl(kickVoteEntity.getStudyMember().getMember().getProfileImageUrl())
        .build();
  }

}
