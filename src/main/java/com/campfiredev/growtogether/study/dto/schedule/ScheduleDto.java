package com.campfiredev.growtogether.study.dto.schedule;

import com.campfiredev.growtogether.study.entity.schedule.ScheduleEntity;
import com.campfiredev.growtogether.study.type.ScheduleType;
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
public class ScheduleDto {

  private Long scheduleId;

  private String title;

  private LocalDate date;

  private LocalTime time;

  private ScheduleType scheduleType;

  private String creator;

  public static ScheduleDto fromEntity(ScheduleEntity scheduleEntity) {
    return ScheduleDto.builder()
        .scheduleId(scheduleEntity.getId())
        .title(scheduleEntity.getTitle())
        .date(scheduleEntity.getDate())
        .time(scheduleEntity.getTime())
        .scheduleType(scheduleEntity.getType())
        .creator(scheduleEntity.getStudyMember().getMember().getNickName())
        .build();
  }

}
