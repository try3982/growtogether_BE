package com.campfiredev.growtogether.study.repository.schedule;

import com.campfiredev.growtogether.study.entity.schedule.ScheduleEntity;
import com.campfiredev.growtogether.study.type.ScheduleType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {

  @Query("SELECT sc FROM ScheduleEntity sc JOIN FETCH sc.studyMember sm "
      + "JOIN FETCH sm.member m "
      + "WHERE sc.study.studyId = :studyId AND sc.date = :date")
  List<ScheduleEntity> findWithMemberByStudyIdAndDate(
      @Param("studyId") Long studyId,
      @Param("date") LocalDate date
  );

  List<ScheduleEntity> findByStudyStudyIdAndTypeAndDateBetween(
      Long studyId, ScheduleType type, LocalDate startDate, LocalDate endDate);


  Optional<ScheduleEntity> findFirstByTypeAndDateAndTimeBetween(ScheduleType type, LocalDate date,
      LocalTime startTime, LocalTime endTime);


  @Query("SELECT DISTINCT sc FROM ScheduleEntity sc " +
      "JOIN FETCH sc.studyMember sm " +
      "JOIN FETCH sm.member m " +
      "WHERE sc.study.studyId = :studyId " +
      "AND sc.date BETWEEN :startDate AND :endDate")
  List<ScheduleEntity> findWithMemberByStudyIdAndDateBetween(
      @Param("studyId") Long studyId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate
  );

  @Query("SELECT DISTINCT s FROM ScheduleEntity s "
      + "LEFT JOIN FETCH s.studyMember sm "
      + "LEFT JOIN FETCH sm.member creator "
      + "LEFT JOIN FETCH s.attendance a "
      + "LEFT JOIN FETCH a.studyMember asm "
      + "LEFT JOIN FETCH asm.member attendee "
      + "WHERE s.study.studyId = :studyId AND s.date BETWEEN :startDate AND :endDate")
  List<ScheduleEntity> findSchedulesWithAttendee(
      @Param("studyId") Long studyId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);


}
