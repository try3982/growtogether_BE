package com.campfiredev.growtogether.study.schedule.repository;

import com.campfiredev.growtogether.study.schedule.entity.ScheduleEntity;
import java.time.LocalDate;
import java.util.List;
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


  @Query("SELECT sc FROM ScheduleEntity sc " +
      "JOIN FETCH sc.studyMember sm " +
      "JOIN FETCH sm.member m " +
      "WHERE sc.study.studyId = :studyId " +
      "AND sc.date BETWEEN :startDate AND :endDate")
  List<ScheduleEntity> findWithMemberByStudyIdAndDateBetween(
      @Param("studyId") Long studyId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate
  );
}
