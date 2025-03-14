package com.campfiredev.growtogether.study.entity.join;

import com.campfiredev.growtogether.common.entity.BaseEntity;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.study.entity.Study;
import com.campfiredev.growtogether.study.type.StudyMemberType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import static com.campfiredev.growtogether.study.type.StudyMemberType.*;

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
        .study(study)
        .member(member)
        .build();
  }

  public void confirm(){
    status = NORMAL;
  }

}

