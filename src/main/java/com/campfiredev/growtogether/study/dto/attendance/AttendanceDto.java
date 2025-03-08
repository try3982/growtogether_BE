package com.campfiredev.growtogether.study.dto.attendance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceDto {
  private Long scheduleId;
  private LocalDate date;
  private LocalTime time;
  private List<String> attendees;
}
