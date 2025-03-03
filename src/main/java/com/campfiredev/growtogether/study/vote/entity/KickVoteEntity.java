package com.campfiredev.growtogether.study.vote.entity;

import static com.campfiredev.growtogether.study.vote.type.VoteType.KICK;

import com.campfiredev.growtogether.study.entity.join.StudyMemberEntity;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@DiscriminatorValue("KICK")
public class KickVoteEntity extends VoteEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "target_id")
  private StudyMemberEntity target;
}
