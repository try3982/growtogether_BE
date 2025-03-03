package com.campfiredev.growtogether.study.vote.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalTime;
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

  private LocalDate date;

  private LocalTime time;
}
