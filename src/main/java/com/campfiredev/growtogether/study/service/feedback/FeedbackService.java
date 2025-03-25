package com.campfiredev.growtogether.study.service.feedback;

import static com.campfiredev.growtogether.exception.response.ErrorCode.*;
import static com.campfiredev.growtogether.study.entity.StudyStatus.COMPLETE;
import static com.campfiredev.growtogether.study.entity.StudyStatus.PROGRESS;
import static com.campfiredev.growtogether.study.type.StudyMemberType.LEADER;
import static com.campfiredev.growtogether.study.type.StudyMemberType.NORMAL;

import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorCode;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedbackService {

  private final FeedbackRepository feedbackRepository;
  private final FeedbackContentRepository feedbackContentRepository;
  private final JoinRepository joinRepository;
  private final RedissonClient redissonClient;

  public void feedback(Long memberId, Long studyId, List<FeedbackCreateDto> feedbacks) {
    StudyMemberEntity studyMemberEntity = getStudyMemberEntity(memberId, studyId);

    validationFeedback(studyMemberEntity);

    FeedbackEntity save = saveFeedback(studyMemberEntity);

    Map<Long, StudyMemberEntity> studyMemberMap = extractStudyMemberMap(feedbacks);

    saveFeedbacks(feedbacks, save, studyMemberMap);

    averageRating(feedbacks, studyMemberMap);
  }

  private Map<Long, StudyMemberEntity> extractStudyMemberMap(List<FeedbackCreateDto> feedbacks) {
    List<Long> ids = feedbacks.stream()
        .map(feedbackCreateDto -> feedbackCreateDto.getStudyMemberId())
        .collect(Collectors.toList());

    List<StudyMemberEntity> studyMembers = joinRepository.findAllWithMembersInIds(ids);

    return studyMembers.stream()
        .collect(Collectors.toMap(studyMemberEntity -> studyMemberEntity.getId(), s -> s));
  }

  private FeedbackEntity saveFeedback(StudyMemberEntity studyMemberEntity) {
    return feedbackRepository.save(FeedbackEntity.builder()
        .studyMember(studyMemberEntity)
        .build());
  }

  private StudyMemberEntity getStudyMemberEntity(Long memberId, Long studyId) {
    return joinRepository.findByStudyAndMemberWithStudyInStatus(
            studyId, memberId, List.of(NORMAL, LEADER))
        .orElseThrow(() -> new CustomException(NOT_A_STUDY_MEMBER));
  }

  private void saveFeedbacks(List<FeedbackCreateDto> feedbacks, FeedbackEntity save,
      Map<Long, StudyMemberEntity> studyMemberMap) {
    List<FeedbackContentEntity> entities = feedbacks.stream()
        .map(dto -> FeedbackContentEntity.builder()
            .feedback(save)
            .studyMember(studyMemberMap.get(dto.getStudyMemberId()))
            .content(dto.getContent())
            .score(dto.getScore())
            .build())
        .collect(Collectors.toList());

    feedbackContentRepository.saveAll(entities);
  }

  private void validationFeedback(StudyMemberEntity studyMemberEntity) {
    if(!COMPLETE.equals(studyMemberEntity.getStudy().getStudyStatus())){
      throw new CustomException(INVALID_FEEDBACK_PERIOD);
    }

    if(feedbackRepository.existsByStudyMember(studyMemberEntity)){
      throw new CustomException(ALREADY_FEEDBACK);
    }
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
        if (isLocked) {
          MemberEntity member = studyMember.getMember();

          Long count = feedbackContentRepository.countByStudyMember(studyMember);

          member.setRating((member.getRating() * (count + 1) + feedback.getScore()) / (count + 2));
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } finally {
        if (isLocked) {
          lock.unlock();
        }
      }
    }
  }


}
