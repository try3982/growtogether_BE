package com.campfiredev.growtogether.study.schedule.entity;

import static com.campfiredev.growtogether.study.schedule.type.ScheduleType.*;

import com.campfiredev.growtogether.study.entity.Study;
import com.campfiredev.growtogether.study.entity.join.StudyMemberEntity;
import com.campfiredev.growtogether.study.schedule.LocalTimeConverter;
import com.campfiredev.growtogether.study.schedule.type.ScheduleType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "schedule")
public class ScheduleEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "schedule_id")
  private Long id;

  @Column(nullable = false)
  @Setter
  private String title;

  @Column(nullable = false)
  @Setter
  private LocalDate date;

  @Column(nullable = false)
  @Setter
  private LocalTime time;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "study_member_id", nullable = false)
  private StudyMemberEntity studyMember;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "study_id", nullable = false)
  private Study study;

  @Enumerated(EnumType.STRING)
  private ScheduleType type;

  public static ScheduleEntity create(StudyMemberEntity studyMember, String title, LocalDate date,
      LocalTime time) {
    return ScheduleEntity.builder()
        .title(title)
        .date(date)
        .time(time)
        .studyMember(studyMember)
        .study(studyMember.getStudy())
        .type(OTHER)
        .build();
  }
}
