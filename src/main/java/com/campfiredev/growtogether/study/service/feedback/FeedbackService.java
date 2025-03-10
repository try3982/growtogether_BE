package com.campfiredev.growtogether.study.service.feedback;

import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.study.dto.feedback.FeedbackCreateDto;
import com.campfiredev.growtogether.study.entity.feedback.FeedbackContentEntity;
import com.campfiredev.growtogether.study.entity.feedback.FeedbackEntity;
import com.campfiredev.growtogether.study.entity.join.StudyMemberEntity;
import com.campfiredev.growtogether.study.repository.feedback.FeedbackContentRepository;
import com.campfiredev.growtogether.study.repository.feedback.FeedbackRepository;
import com.campfiredev.growtogether.study.repository.join.JoinRepository;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {

  private final FeedbackRepository feedbackRepository;
  private final FeedbackContentRepository feedbackContentRepository;
  private final JoinRepository joinRepository;
  private final RedissonClient redissonClient;

  public void feedback(Long userId, Long studyId, List<FeedbackCreateDto> feedbacks) {
    //userId, studyId로 studyMemberEntity 조회
    //joinRepository.findByMemberIdAndStudyIdInStatus()
    StudyMemberEntity studyMemberEntity = new StudyMemberEntity();

    FeedbackEntity save = feedbackRepository.save(FeedbackEntity.builder()
        .studyMember(studyMemberEntity)
        .build());

    List<Long> ids = feedbacks.stream()
        .map(feedback -> feedback.getStudyMemberId())
        .collect(Collectors.toList());

    //fetchjoin으로 member 같이 가져올 것
    List<StudyMemberEntity> studyMembers = joinRepository.findAllById(ids);

    Map<Long, StudyMemberEntity> studyMemberMap = studyMembers.stream()
        .collect(Collectors.toMap(sm -> sm.getId(), s -> s));

    List<FeedbackContentEntity> entities = feedbacks.stream()
        .map(dto -> FeedbackContentEntity.builder()
            .feedback(save)
            .studyMember(studyMemberMap.get(dto.getStudyMemberId()))
            .content(dto.getContent())
            .score(dto.getScore())
            .build())
        .collect(Collectors.toList());

    feedbackContentRepository.saveAll(entities);

    averageRating(feedbacks, studyMemberMap);
  }

  private void averageRating(List<FeedbackCreateDto> feedbacks,
      Map<Long, StudyMemberEntity> studyMemberMap) {
    for (FeedbackCreateDto feedback : feedbacks) {
      StudyMemberEntity studyMember = studyMemberMap.get(feedback.getStudyMemberId());

      String lockKey = "feedback" + studyMember;
      RLock lock = redissonClient.getLock(lockKey);

      boolean isLocked = false;
      try {
        isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS);
        if(isLocked){
          MemberEntity member = studyMember.getMember();

          Long count = feedbackContentRepository.countByStudyMember(studyMember);

          member.setRating((member.getRating() * (count+1) + feedback.getScore()) / (count + 2));
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }finally{
        if(isLocked){
          lock.unlock();
        }
      }
    }
  }


}
