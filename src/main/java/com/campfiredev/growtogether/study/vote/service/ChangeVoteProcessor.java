package com.campfiredev.growtogether.study.vote.service;

import com.campfiredev.growtogether.study.vote.entity.ChangeVoteEntity;
import com.campfiredev.growtogether.study.vote.entity.VoteEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class ChangeVoteProcessor implements VoteProcessor {

  //임시 테스트용
  @Override
  @Transactional
  public void processVote(VoteEntity voteEntity, int votes, int totalSize) {
    if (votes >= totalSize) {
      ChangeVoteEntity changeVoteEntity = (ChangeVoteEntity) voteEntity;
      log.info("CHANGE 투표 통과: " + changeVoteEntity.getDate());
      log.info("시간 변경: " + changeVoteEntity.getTime());
    }
  }
}
