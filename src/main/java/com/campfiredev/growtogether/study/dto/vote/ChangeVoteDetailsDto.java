package com.campfiredev.growtogether.study.dto.vote;

import com.campfiredev.growtogether.study.entity.vote.ChangeVoteEntity;
import com.campfiredev.growtogether.study.type.VoteType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChangeVoteDetailsDto extends VoteDto{

  private String content;

  private LocalDateTime prevStartDate;

  private LocalDateTime prevEndDate;

  private Integer prevTotalTime;

  private LocalDateTime changeStartDate;

  private LocalDateTime changeEndDate;

  private Integer changeTotalTime;

  public static ChangeVoteDetailsDto fromEntity(ChangeVoteEntity changeVoteEntity) {
    return ChangeVoteDetailsDto.builder()
        .voteId(changeVoteEntity.getId())
        .title(changeVoteEntity.getTitle())
        .voteType(VoteType.CHANGE)
        .content(changeVoteEntity.getContent())
        .prevStartDate(changeVoteEntity.getSchedule().getStart())
        .prevEndDate(changeVoteEntity.getSchedule().getEnd())
        .prevTotalTime(changeVoteEntity.getSchedule().getTotalTime())
        .changeStartDate(changeVoteEntity.getStart())
        .changeEndDate(changeVoteEntity.getEnd())
        .changeTotalTime(changeVoteEntity.getTotal())
        .build();

  }

}
