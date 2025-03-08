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

    ScheduleEntity scheduleEntity = scheduleRepository.findFirstByTypeAndDateAndTimeBetween(MAIN,
            LocalDate.now(),
            LocalTime.now().minusMinutes(10), LocalTime.now().plusMinutes(10))
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

    List<ScheduleEntity> schedules = scheduleRepository.findByStudyStudyIdAndTypeAndDateBetween(
        studyId, MAIN, startDate, endDate);

    List<AttendanceEntity> attendee = attendanceRepository.findAttendancesBySchedules(
        schedules);

    Map<Long, AttendanceDto> scheduleAttendanceMap = schedules.stream()
        .collect(Collectors.toMap(
            scheduleEntity -> scheduleEntity.getId(),
            schedule -> new AttendanceDto(schedule.getId(), schedule.getDate(), schedule.getTime(),
                new ArrayList<>())
        ));

    attendee.forEach(attendance ->
        scheduleAttendanceMap.get(attendance.getSchedule().getId())
            .getAttendees().add(attendance.getStudyMember().getMember().getNickName())
    );

    return new ArrayList<>(scheduleAttendanceMap.values());
  }


}
