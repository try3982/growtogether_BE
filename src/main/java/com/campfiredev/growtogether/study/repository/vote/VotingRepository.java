package com.campfiredev.growtogether.study.repository.vote;

import com.campfiredev.growtogether.study.entity.join.StudyMemberEntity;
import com.campfiredev.growtogether.study.entity.vote.VoteEntity;
import com.campfiredev.growtogether.study.entity.vote.VotingEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface VotingRepository extends JpaRepository<VotingEntity, Long> {

  boolean existsByVoteAndStudyMember(VoteEntity vote, StudyMemberEntity studyMember);
}
