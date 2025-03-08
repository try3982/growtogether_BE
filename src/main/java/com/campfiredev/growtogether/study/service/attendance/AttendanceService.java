package com.campfiredev.growtogether.study.service.attendance;

import static com.campfiredev.growtogether.study.type.ScheduleType.*;
import static com.campfiredev.growtogether.study.type.StudyMemberType.LEADER;
import static com.campfiredev.growtogether.study.type.StudyMemberType.NORMAL;

import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorCode;
import com.campfiredev.growtogether.study.dto.attendance.AttendanceDto;
import com.campfiredev.growtogether.study.entity.attendance.AttendanceEntity;
import com.campfiredev.growtogether.study.entity.join.StudyMemberEntity;
import com.campfiredev.growtogether.study.entity.schedule.ScheduleEntity;
import com.campfiredev.growtogether.study.repository.attendance.AttendanceRepository;
import com.campfiredev.growtogether.study.repository.join.JoinRepository;
import com.campfiredev.growtogether.study.repository.schedule.ScheduleRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceService {

  private final AttendanceRepository attendanceRepository;
  private final ScheduleRepository scheduleRepository;
  private final JoinRepository joinRepository;

  public void attendance(Long memberId, Long studyId) {

    StudyMemberEntity studyMemberEntity = joinRepository.findByMemberIdAndStudyIdInStatus(memberId,
            studyId, List.of(NORMAL, LEADER))
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_A_STUDY_MEMBER));

    LocalDateTime now = LocalDateTime.now();

    ScheduleEntity scheduleEntity = scheduleRepository.findFirstByTypeAndStartBetween(
            MAIN, now.minusMinutes(10), now.plusMinutes(10))
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_ATTENDANCE_TIME));

    if (attendanceRepository.existsByStudyMemberIdAndScheduleId(studyMemberEntity.getId(),
        scheduleEntity.getId())) {
      throw new CustomException(ErrorCode.ALREADY_ATTENDANCE);
    }

    attendanceRepository.save(AttendanceEntity.create(studyMemberEntity, scheduleEntity));
  }

  public List<AttendanceDto> getAttendee(Long studyId, String date) {
    YearMonth yearMonth = YearMonth.parse(date);

    LocalDate startDate = yearMonth.atDay(1);
    LocalDate endDate = yearMonth.atEndOfMonth();

    List<AttendanceEntity> attendees = attendanceRepository.findAttendancesBetween(
        studyId, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));

    Map<Long, List<String>> collect = attendees.stream()
        .collect(
            Collectors.groupingBy(attendee -> attendee.getSchedule().getId(),
                Collectors.mapping(attendee -> attendee.getStudyMember().getMember().getNickName(),
                    Collectors.toList())));

    return collect.entrySet().stream()
        .map(entry -> {
          ScheduleEntity scheduleEntity = attendees.stream()
              .filter(attendee -> attendee.getSchedule().getId().equals(entry.getKey()))
              .findFirst().map(attendee -> attendee.getSchedule()).orElseThrow();

          return AttendanceDto.builder()
              .scheduleId(scheduleEntity.getId())
              .date(scheduleEntity.getStart())
              .attendees(entry.getValue())
              .build();
        }).collect(Collectors.toList());
  }


}
