package com.campfiredev.growtogether.study.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ScheduleCreateDto {

  @NotBlank
  private String title;

  @NotNull
  private LocalDate date;

  @NotNull
  private LocalTime time;
}
