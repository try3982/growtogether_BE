package com.campfiredev.growtogether.study.vote.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateScheduleDto {

  private String content;

  private LocalDate date;

  private LocalTime time;

}
