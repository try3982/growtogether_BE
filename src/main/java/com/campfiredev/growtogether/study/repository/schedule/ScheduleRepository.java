package com.campfiredev.growtogether.study.repository.schedule;

import com.campfiredev.growtogether.study.entity.Study;
import com.campfiredev.growtogether.study.entity.schedule.ScheduleEntity;
import com.campfiredev.growtogether.study.type.ScheduleType;
import jakarta.ejb.Schedule;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {

  @Query("SELECT sc FROM ScheduleEntity sc JOIN FETCH sc.studyMember sm "
      + "JOIN FETCH sm.member m "
      + "WHERE sc.study.studyId = :studyId "
      + "AND sc.start BETWEEN :startOfDay AND :endOfDay")
  List<ScheduleEntity> findWithMemberByStudyIdAndDate(
      @Param("studyId") Long studyId,
      @Param("startOfDay") LocalDateTime startOfDay,
      @Param("endOfDay") LocalDateTime endOfDay
  );


  Optional<ScheduleEntity> findFirstByTypeAndStartBetween(ScheduleType type,
      LocalDateTime start, LocalDateTime end);


  @Query("SELECT DISTINCT sc FROM ScheduleEntity sc " +
      "JOIN FETCH sc.studyMember sm " +
      "JOIN FETCH sm.member m " +
      "WHERE sc.study.studyId = :studyId " +
      "AND sc.start BETWEEN :startDate AND :endDate")
  List<ScheduleEntity> findWithMemberByStudyIdAndDateBetween(
      @Param("studyId") Long studyId,
      @Param("startDate") LocalDateTime start,
      @Param("endDate") LocalDateTime end
  );

  @Query("SELECT DISTINCT s FROM ScheduleEntity s "
      + "LEFT JOIN FETCH s.studyMember sm "
      + "LEFT JOIN FETCH sm.member creator "
      + "LEFT JOIN FETCH s.attendance a "
      + "LEFT JOIN FETCH a.studyMember asm "
      + "LEFT JOIN FETCH asm.member attendee "
      + "WHERE s.study.studyId = :studyId AND s.start BETWEEN :start AND :end")
  List<ScheduleEntity> findSchedulesWithAttendee(
      @Param("studyId") Long studyId,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);

  List<ScheduleEntity> findByStudyAndStartBetweenAndType(Study study, LocalDateTime start,
      LocalDateTime end, ScheduleType type);


}
