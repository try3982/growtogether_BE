package com.campfiredev.growtogether.study.dto.schedule;

import com.campfiredev.growtogether.study.entity.schedule.ScheduleEntity;
import com.campfiredev.growtogether.study.type.ScheduleType;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class ScheduleAttendeeDto {

  private Long scheduleId;
  private String title;
  private LocalDateTime start;
  private LocalDateTime end;
  private Integer totalTime;
  private ScheduleType type;
  private String creator;
  private List<String> attendedNicknames;

  public static ScheduleAttendeeDto create(ScheduleEntity schedule, String nickName, List<String> attendedNicknames) {
    return ScheduleAttendeeDto.builder()
        .scheduleId(schedule.getId())
        .title(schedule.getTitle())
        .start(schedule.getStart())
        .end(schedule.getEnd())
        .totalTime(schedule.getTotalTime())
        .type(schedule.getType())
        .creator(nickName)
        .attendedNicknames(attendedNicknames)
        .build();
  }
}
