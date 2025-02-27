package com.campfiredev.growtogether.study.repository.join;

import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.study.entity.Study;
import com.campfiredev.growtogether.study.entity.join.StudyMemberEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JoinRepository extends JpaRepository<StudyMemberEntity, Long> {

  Optional<StudyMemberEntity> findByMemberAndStudy(MemberEntity member, Study study);

  @Query("SELECT sm FROM StudyMemberEntity sm " +
      "JOIN FETCH sm.study s " +
      "JOIN FETCH sm.member u " +
      "WHERE sm.id = :studyMemberId")
  Optional<StudyMemberEntity> findWithStudyAndMemberById(@Param("studyMemberId") Long studyMemberId);

  @Query("SELECT sm.member.userId FROM StudyMemberEntity sm WHERE sm.id = :studyMemberId")
  Long getMemberIdByStudyMemberId(@Param("studyMemberId") Long studyMemberId);
}

