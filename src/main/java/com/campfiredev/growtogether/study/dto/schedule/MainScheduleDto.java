package com.campfiredev.growtogether.study.dto.schedule;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;

@Getter
public class MainScheduleDto {
  LocalDateTime startTime;
  LocalDateTime endTime;

  public MainScheduleDto(String date, String time, int total) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    this.startTime = LocalDateTime.parse(date + " " + time, formatter);
    this.endTime = this.startTime.plusMinutes(total);
  }
}
