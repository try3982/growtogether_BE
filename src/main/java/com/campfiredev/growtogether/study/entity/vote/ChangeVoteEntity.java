package com.campfiredev.growtogether.study.entity.vote;

import static com.campfiredev.growtogether.study.type.VoteStatus.PROGRESS;

import com.campfiredev.growtogether.study.dto.schedule.ScheduleUpdateDto;
import com.campfiredev.growtogether.study.entity.join.StudyMemberEntity;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@DiscriminatorValue("CHANGE")
public class ChangeVoteEntity extends VoteEntity {

  //일정 관리 id
  //추가 예정
  private Long scheduleId;

  private String content;

  private LocalDateTime start;

  private LocalDateTime end;

  private Integer total;

  public static ChangeVoteEntity create(String title, StudyMemberEntity studyMemberEntity,
     ScheduleUpdateDto scheduleUpdateDto, Long scheduleId) {

    LocalDateTime start = LocalDateTime.of(scheduleUpdateDto.getStartDate(),
        scheduleUpdateDto.getStartTime());

    return ChangeVoteEntity.builder()
        .title(title)
        .scheduleId(scheduleId)
        .studyMember(studyMemberEntity)
        .study(studyMemberEntity.getStudy())
        .status(PROGRESS)
        .content(scheduleUpdateDto.getTitle())
        .start(start)
        .end(start.plusMinutes(scheduleUpdateDto.getTotalTime()))
        .total(scheduleUpdateDto.getTotalTime())
        .build();
  }
}
