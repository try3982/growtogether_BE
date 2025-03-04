package com.campfiredev.growtogether.study.vote.service;

import static com.campfiredev.growtogether.study.type.StudyMemberType.KICK;

import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorCode;
import com.campfiredev.growtogether.study.entity.join.StudyMemberEntity;
import com.campfiredev.growtogether.study.repository.join.JoinRepository;
import com.campfiredev.growtogether.study.vote.entity.KickVoteEntity;
import com.campfiredev.growtogether.study.vote.entity.VoteEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class KickVoteProcessor implements VoteProcessor {

  private final JoinRepository joinRepository;

  public KickVoteProcessor(JoinRepository joinRepository) {
    this.joinRepository = joinRepository;
  }

  @Override
  @Transactional
  public void processVote(VoteEntity voteEntity, int votes, int totalSize) {
    if (votes > totalSize / 2) {
      KickVoteEntity kickVoteEntity = (KickVoteEntity) voteEntity;
      StudyMemberEntity studyMemberEntity = joinRepository.findById(kickVoteEntity.getTarget().getId())
          .orElseThrow(() -> new CustomException(ErrorCode.NOT_A_STUDY_MEMBER));

      studyMemberEntity.setStatus(KICK);
      log.info("KICK 투표 통과: " + studyMemberEntity.getId() + " 강퇴됨");
    }
  }
}
