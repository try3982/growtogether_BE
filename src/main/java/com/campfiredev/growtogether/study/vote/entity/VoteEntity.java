package com.campfiredev.growtogether.study.vote.entity;

import com.campfiredev.growtogether.common.entity.BaseEntity;
import com.campfiredev.growtogether.study.entity.Study;
import com.campfiredev.growtogether.study.entity.join.StudyMemberEntity;
import com.campfiredev.growtogether.study.vote.type.VoteStatus;
import com.campfiredev.growtogether.study.vote.type.VoteType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vote")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "vote_type", discriminatorType = DiscriminatorType.STRING)
public class VoteEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "vote_id")
  private Long id;

  private String title;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "study_id", nullable = false)
  private Study study;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "study_member_id", nullable = false)
  private StudyMemberEntity studyMember;

  @Setter
  @Enumerated(EnumType.STRING)
  private VoteStatus status;
}
