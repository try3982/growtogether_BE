package com.campfiredev.growtogether.study.entity.join;

import static com.campfiredev.growtogether.study.type.StudyMemberType.NORMAL;
import static com.campfiredev.growtogether.study.type.StudyMemberType.PENDING;

import com.campfiredev.growtogether.common.entity.BaseEntity;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.study.entity.Study;
import com.campfiredev.growtogether.study.type.StudyMemberType;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "study_member",
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_study_user", columnNames = {"study_id", "member_id"})
    }
)
public class StudyMemberEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "study_member_id")
  private Long id;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private StudyMemberType status;

  @Column(nullable = false)
  private Integer studyPoint;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "study_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Study study;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private MemberEntity member;

  public static StudyMemberEntity create(Study study, MemberEntity member){
    return StudyMemberEntity.builder()
        .status(PENDING)
        .studyPoint(0)
        .study(study)
        .member(member)
        .build();
  }

  public void confirm(){
    status = NORMAL;
  }

}

