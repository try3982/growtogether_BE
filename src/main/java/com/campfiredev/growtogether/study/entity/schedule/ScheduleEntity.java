package com.campfiredev.growtogether.study.entity.schedule;

import static com.campfiredev.growtogether.study.type.ScheduleType.*;

import com.campfiredev.growtogether.study.dto.schedule.ScheduleCreateDto;
import com.campfiredev.growtogether.study.entity.Study;
import com.campfiredev.growtogether.study.entity.attendance.AttendanceEntity;
import com.campfiredev.growtogether.study.entity.join.StudyMemberEntity;
import com.campfiredev.growtogether.study.type.ScheduleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
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
  private LocalDateTime start;

  @Column(nullable = false)
  @Setter
  private LocalDateTime end;

  @Column(nullable = false)
  @Setter
  private Integer totalTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "study_member_id", nullable = false)
  private StudyMemberEntity studyMember;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "study_id", nullable = false)
  private Study study;

  @Enumerated(EnumType.STRING)
  private ScheduleType type;

  @OneToMany(mappedBy = "schedule")
  private List<AttendanceEntity> attendance;

  public static ScheduleEntity create(StudyMemberEntity studyMember, ScheduleCreateDto scheduleCreateDto) {

    LocalDateTime start = LocalDateTime.of(scheduleCreateDto.getStartDate(), scheduleCreateDto.getStartTime());
    LocalDateTime end = start.plusMinutes(scheduleCreateDto.getTotalTime());

    return ScheduleEntity.builder()
        .title(scheduleCreateDto.getTitle())
        .start(start)
        .end(end)
        .studyMember(studyMember)
        .study(studyMember.getStudy())
        .totalTime(scheduleCreateDto.getTotalTime())
        .type(OTHER)
        .build();
  }
}
