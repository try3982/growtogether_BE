package com.campfiredev.growtogether.study.service.vote;

import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorCode;
import com.campfiredev.growtogether.study.entity.schedule.ScheduleEntity;
import com.campfiredev.growtogether.study.repository.schedule.ScheduleRepository;
import com.campfiredev.growtogether.study.entity.vote.ChangeVoteEntity;
import com.campfiredev.growtogether.study.entity.vote.VoteEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChangeVoteProcessor implements VoteProcessor {

  private final ScheduleRepository scheduleRepository;
  private final RedisTemplate<String, Object> redisTemplate;

  @Override
  @Transactional
  public void processVote(VoteEntity voteEntity, Long votes, Long totalSize) {
    if (votes >= totalSize) {
      ChangeVoteEntity changeVoteEntity = (ChangeVoteEntity) voteEntity;
      log.info("CHANGE 투표 통과: " + changeVoteEntity.getStart());
      log.info("시간 변경: " + changeVoteEntity.getEnd());

      ScheduleEntity scheduleEntity = scheduleRepository.findById(changeVoteEntity.getScheduleId())
          .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

      scheduleEntity.setTitle(changeVoteEntity.getContent());
      scheduleEntity.setStart(changeVoteEntity.getStart());
      scheduleEntity.setEnd(changeVoteEntity.getEnd());
      scheduleEntity.setTotalTime(changeVoteEntity.getTotal());
    }
    redisTemplate.delete("vote" + voteEntity.getId());
  }
}
