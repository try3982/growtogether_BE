package com.campfiredev.growtogether.study.repository.vote;

import com.campfiredev.growtogether.study.entity.vote.VoteEntity;
import com.campfiredev.growtogether.study.type.VoteStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<VoteEntity, Long> {

  List<VoteEntity> findByStudy_StudyIdAndStatus(Long studyId, VoteStatus status);

  @Query("SELECT v FROM VoteEntity v JOIN FETCH v.study WHERE v.id = :voteId")
  Optional<VoteEntity> findVoteAndStudyById(@Param("voteId") Long voteId);

}
