package com.campfiredev.growtogether.study.vote.repository;

import com.campfiredev.growtogether.study.entity.join.StudyMemberEntity;
import com.campfiredev.growtogether.study.vote.entity.VoteEntity;
import com.campfiredev.growtogether.study.vote.entity.VotingEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface VotingRepository extends JpaRepository<VotingEntity, Long> {

  boolean existsByVoteAndStudyMember(VoteEntity vote, StudyMemberEntity studyMember);
}
