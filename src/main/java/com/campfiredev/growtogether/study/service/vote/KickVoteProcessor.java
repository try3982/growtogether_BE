package com.campfiredev.growtogether.study.service.vote;

import static com.campfiredev.growtogether.notification.type.NotiType.VOTE;
import static com.campfiredev.growtogether.study.type.StudyMemberType.KICK;
import static com.campfiredev.growtogether.study.type.StudyMemberType.LEADER;
import static com.campfiredev.growtogether.study.type.StudyMemberType.NORMAL;

import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorCode;
import com.campfiredev.growtogether.notification.service.NotificationService;
import com.campfiredev.growtogether.study.entity.join.StudyMemberEntity;
import com.campfiredev.growtogether.study.repository.join.JoinRepository;
import com.campfiredev.growtogether.study.entity.vote.KickVoteEntity;
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
public class KickVoteProcessor implements VoteProcessor {

  private final JoinRepository joinRepository;
  private final RedisTemplate<String, Object> redisTemplate;
  private final NotificationService notificationService;

  @Override
  @Transactional
  public void processVote(VoteEntity voteEntity, Long votes, Long totalSize) {
    KickVoteEntity kickVoteEntity = (KickVoteEntity) voteEntity;
    if (votes > totalSize / 2) {
      //fetchJoin으로 바꿀것
      StudyMemberEntity studyMemberEntity = joinRepository.findById(kickVoteEntity.getTarget().getId())
          .orElseThrow(() -> new CustomException(ErrorCode.NOT_A_STUDY_MEMBER));

      studyMemberEntity.setStatus(KICK);
      log.info("KICK 투표 통과: " + studyMemberEntity.getId() + " 강퇴됨");

      notificationService.sendNotification(studyMemberEntity.getMember(),kickVoteEntity.getTarget().getMember().getNickName() + "님이 강퇴 되었습니다.", null,VOTE);
    }else{
      joinRepository.findByStudyAndStatusIn(kickVoteEntity.getStudy(), List.of(LEADER, NORMAL))
          .stream()
          .map(studyMemberEntity -> studyMemberEntity.getMember())
          .forEach(member -> notificationService.sendNotification(member, "강퇴 투표가 부결되었습니다.", null, VOTE));
    }

    redisTemplate.delete("vote" + voteEntity.getId());
  }
}
