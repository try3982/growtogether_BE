package com.campfiredev.growtogether.study.repository.attendance;

import com.campfiredev.growtogether.study.entity.attendance.AttendanceEntity;
import com.campfiredev.growtogether.study.entity.schedule.ScheduleEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Long> {

  boolean existsByStudyMemberIdAndScheduleId(Long studyMemberId, Long scheduleId);

  List<AttendanceEntity> findByScheduleIn(List<ScheduleEntity> scheduleIds);

  @Query("SELECT DISTINCT a FROM AttendanceEntity a "
      + "LEFT JOIN FETCH a.studyMember sm "
      + "LEFT JOIN FETCH sm.member attendee "
      + "WHERE a.schedule IN :schedules")
  List<AttendanceEntity> findAttendancesBySchedules(
      @Param("schedules") List<ScheduleEntity> schedules);


}
