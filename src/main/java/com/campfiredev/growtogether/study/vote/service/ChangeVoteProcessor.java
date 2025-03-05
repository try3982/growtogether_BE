package com.campfiredev.growtogether.study.vote.service;

import com.amazonaws.services.kms.model.CloudHsmClusterInUseException;
import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorCode;
import com.campfiredev.growtogether.study.schedule.entity.ScheduleEntity;
import com.campfiredev.growtogether.study.schedule.repository.ScheduleRepository;
import com.campfiredev.growtogether.study.vote.entity.ChangeVoteEntity;
import com.campfiredev.growtogether.study.vote.entity.VoteEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChangeVoteProcessor implements VoteProcessor {

  private final ScheduleRepository scheduleRepository;

  //임시 테스트용
  @Override
  @Transactional
  public void processVote(VoteEntity voteEntity, int votes, int totalSize) {
    if (votes >= totalSize) {
      ChangeVoteEntity changeVoteEntity = (ChangeVoteEntity) voteEntity;
      log.info("CHANGE 투표 통과: " + changeVoteEntity.getDate());
      log.info("시간 변경: " + changeVoteEntity.getTime());

      ScheduleEntity scheduleEntity = scheduleRepository.findById(changeVoteEntity.getScheduleId())
          .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

      scheduleEntity.setTitle(changeVoteEntity.getContent());
      scheduleEntity.setDate(changeVoteEntity.getDate());
      scheduleEntity.setTime(changeVoteEntity.getTime());
    }
  }
}
