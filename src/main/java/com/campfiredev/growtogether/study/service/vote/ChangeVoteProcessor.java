package com.campfiredev.growtogether.study.service.vote;

import static com.campfiredev.growtogether.notification.type.NotiType.VOTE;
import static com.campfiredev.growtogether.study.type.StudyMemberType.LEADER;
import static com.campfiredev.growtogether.study.type.StudyMemberType.NORMAL;

import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorCode;
import com.campfiredev.growtogether.notification.service.NotificationService;
import com.campfiredev.growtogether.study.entity.join.StudyMemberEntity;
import com.campfiredev.growtogether.study.entity.schedule.ScheduleEntity;
import com.campfiredev.growtogether.study.repository.join.JoinRepository;
import com.campfiredev.growtogether.study.repository.schedule.ScheduleRepository;
import com.campfiredev.growtogether.study.entity.vote.ChangeVoteEntity;
import com.campfiredev.growtogether.study.entity.vote.VoteEntity;
import java.util.List;
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
  private final NotificationService notificationService;
  private final JoinRepository joinRepository;

  @Override
  @Transactional
  public void processVote(VoteEntity voteEntity, Long votes, Long totalSize) {
    ChangeVoteEntity changeVoteEntity = (ChangeVoteEntity) voteEntity;

    List<StudyMemberEntity> memberList = joinRepository.findByStudyAndStatusIn(
        changeVoteEntity.getStudy(), List.of(LEADER, NORMAL));

    if (votes >= totalSize) {

      log.info("CHANGE 투표 통과: " + changeVoteEntity.getStart());
      log.info("시간 변경: " + changeVoteEntity.getEnd());

      ScheduleEntity scheduleEntity = scheduleRepository.findById(changeVoteEntity.getSchedule().getId())
          .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

      scheduleEntity.setTitle(changeVoteEntity.getContent());
      scheduleEntity.setStart(changeVoteEntity.getStart());
      scheduleEntity.setEnd(changeVoteEntity.getEnd());
      scheduleEntity.setTotalTime(changeVoteEntity.getTotal());

      memberList.stream()
          .map(studyMemberEntity -> studyMemberEntity.getMember())
          .forEach(member -> notificationService.sendNotification(member, "메인 일정이 변경되었습니다.", null,
              VOTE));
    }else{
      memberList.stream()
          .map(studyMemberEntity -> studyMemberEntity.getMember())
          .forEach(member -> notificationService.sendNotification(member, "메인 일정 변경 투표가 부결되었습니다.", null, VOTE));
    }
    redisTemplate.delete("vote" + voteEntity.getId());
  }
}
