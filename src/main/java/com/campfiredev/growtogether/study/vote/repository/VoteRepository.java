package com.campfiredev.growtogether.study.vote.repository;

import com.campfiredev.growtogether.study.vote.entity.VoteEntity;
import com.campfiredev.growtogether.study.vote.type.VoteStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<VoteEntity, Long> {

  @Query("SELECT v FROM VoteEntity v WHERE v.study.studyId = :studyId AND v.status = :status")
  List<VoteEntity> findVoteInProgressByStudyId(@Param("studyId") Long studyId,
      @Param("status") VoteStatus status);

  @Query("SELECT v FROM VoteEntity v JOIN FETCH v.study WHERE v.id = :voteId")
  Optional<VoteEntity> findVoteAndStudyById(@Param("voteId") Long voteId);

}
