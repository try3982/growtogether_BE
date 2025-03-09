package com.campfiredev.growtogether.study.service.vote;

import static com.campfiredev.growtogether.exception.response.ErrorCode.*;
import static com.campfiredev.growtogether.study.type.StudyMemberType.LEADER;
import static com.campfiredev.growtogether.study.type.StudyMemberType.NORMAL;
import static com.campfiredev.growtogether.study.type.VoteStatus.COMPLETE;
import static com.campfiredev.growtogether.study.type.VoteStatus.PROGRESS;

import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorCode;
import com.campfiredev.growtogether.study.entity.join.StudyMemberEntity;
import com.campfiredev.growtogether.study.repository.join.JoinRepository;
import com.campfiredev.growtogether.study.dto.schedule.ScheduleUpdateDto;
import com.campfiredev.growtogether.study.dto.vote.VotingDto;
import com.campfiredev.growtogether.study.dto.vote.VoteCreateDto;
import com.campfiredev.growtogether.study.dto.vote.VoteDto;
import com.campfiredev.growtogether.study.entity.vote.ChangeVoteEntity;
import com.campfiredev.growtogether.study.entity.vote.KickVoteEntity;
import com.campfiredev.growtogether.study.entity.vote.VoteEntity;
import com.campfiredev.growtogether.study.entity.vote.VotingEntity;
import com.campfiredev.growtogether.study.repository.vote.ChangeVoteRepository;
import com.campfiredev.growtogether.study.repository.vote.KickVoteRepository;
import com.campfiredev.growtogether.study.repository.vote.VoteRepository;
import com.campfiredev.growtogether.study.repository.vote.VotingRepository;
import jakarta.persistence.DiscriminatorValue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VoteService {

  private final JoinRepository joinRepository;
  private final VoteRepository voteRepository;
  private final SchedulerService schedulerService;
  private final VotingRepository votingRepository;
  private final KickVoteRepository kickVoteRepository;
  private final ChangeVoteRepository changeVoteRepository;
  private final RedisTemplate<String, Object> redisTemplate;
  private final VoteProcessorFactory voteProcessorFactory;
  private final Scheduler scheduler;

  public void createKickVote(Long memberId, Long studyId, VoteCreateDto voteCreateDto) {

    StudyMemberEntity studyMemberEntity = getStudyMemberEntity(memberId, studyId);

    StudyMemberEntity voted = joinRepository.findById(voteCreateDto.getStudyMemberId())
        .orElseThrow(() -> new CustomException(NOT_A_STUDY_MEMBER));

    String title = voted.getMember().getNickName() + "님의 대한 추방 투표입니다.";

    KickVoteEntity save = kickVoteRepository.save(
        KickVoteEntity.create(title, studyMemberEntity, voted));

    settingVote(studyId, save.getId());

    scheduleJob(KickVoteJob.class, "job"+save.getId(), "job", 3, save.getId());
  }

  public void createChangeVote(Long memberId, Long studyId, Long scheduleId,
      ScheduleUpdateDto scheduleUpdateDto) {
    StudyMemberEntity studyMemberEntity = getStudyMemberEntity(memberId, studyId);

    String title = scheduleId + "번 스케줄 시간 변경 투표입니다.";

    ChangeVoteEntity save = changeVoteRepository.save(
        ChangeVoteEntity.create(title, studyMemberEntity, scheduleUpdateDto, scheduleId));

    settingVote(studyId, save.getId());

    scheduleJob(ChangeVoteJob.class, "job"+save.getId(), "job", 3, save.getId());
  }

  private void scheduleJob(Class<? extends Job> jobClass, String jobName, String jobGroup,
      long delayMinutes, Long voteId) {
    Map<String, Object> data = new HashMap<>();
    data.put("id", voteId);

    schedulerService.scheduleJob(jobClass, jobName, jobGroup, delayMinutes, data);
  }

  public void vote(Long memberId, Long voteId, VotingDto votingDto) {
    VoteEntity voteEntity = voteRepository.findById(voteId)
        .orElseThrow(() -> new CustomException(VOTE_NOT_FOUND));

    StudyMemberEntity studyMemberEntity = getStudyMemberEntity(memberId,
        voteEntity.getStudy().getStudyId());

    validateVote(voteEntity, studyMemberEntity);

    votingRepository.save(VotingEntity.create(voteEntity, studyMemberEntity));

    saveInRedis(voteEntity, votingDto);
  }

  private StudyMemberEntity getStudyMemberEntity(Long memberId, Long studyId) {
    return joinRepository.findByMember_MemberIdAndStudy_StudyIdAndStatusIn(memberId, studyId,
            List.of(LEADER, NORMAL))
        .orElseThrow(() -> new CustomException(NOT_A_STUDY_MEMBER));
  }

  private void validateVote(VoteEntity voteEntity, StudyMemberEntity studyMemberEntity) {
    if (!LEADER.equals(studyMemberEntity.getStatus()) && !NORMAL.equals(
        studyMemberEntity.getStatus())) {
      throw new CustomException(STUDY_MEMBER_ONLY);
    }

    if (votingRepository.existsByVoteAndStudyMember(voteEntity, studyMemberEntity)) {
      throw new CustomException(VOTING_ALREADY_EXISTS);
    }

    if (COMPLETE.equals(voteEntity.getStatus())) {
      throw new CustomException(VOTE_ALREADY_COMPLETE);
    }
  }

  private void settingVote(Long studyId, Long voteId) {
    long count = joinRepository.countByStudy_StudyIdAndStatusIn(studyId, List.of(LEADER, NORMAL));
    redisTemplate.opsForHash().increment("vote" + voteId, "size", count);
  }

  private void saveInRedis(VoteEntity voteEntity, VotingDto votingDto) {
    String key = "vote" + voteEntity.getId();
    Long agree = 0L;
    if (votingDto.isAgree()) {
      agree = redisTemplate.opsForHash().increment(key, "agree", 1);
    }
    Long total = redisTemplate.opsForHash().increment(key, "total", 1);

    Long size = Optional.ofNullable(redisTemplate.opsForHash().get(key, "size"))
        .map(obj -> ((Number) obj).longValue())
        .orElse(0L);

    if(total >= size){
      log.info("즉시 실행");
      sumKickVote(voteEntity.getId());
      return;
    }

    if(voteEntity.getClass().getAnnotation(DiscriminatorValue.class).value().equals("KICK")){
      if(agree > size / 2){
        log.info("강퇴 즉시 실행");
        sumKickVote(voteEntity.getId());
      }
    }
  }

  public void sumKickVote(Long voteId) {
    VoteEntity voteEntity = voteRepository.findVoteAndStudyById(voteId)
        .orElseThrow(() -> new CustomException(ErrorCode.VOTE_NOT_FOUND));

    String key = "vote" + voteId;

    Long size = Optional.ofNullable(redisTemplate.opsForHash().get(key, "size"))
        .map(obj -> ((Number) obj).longValue())
        .orElse(0L);

    Long agree = Optional.ofNullable(redisTemplate.opsForHash().get(key, "agree"))
        .map(obj -> ((Number) obj).longValue())
        .orElse(0L);

    VoteProcessor processor = voteProcessorFactory.getProcessor(voteEntity.getClass());

    if (size > 0) {
        processor.processVote(voteEntity, agree, size);
    }
    voteEntity.setStatus(COMPLETE);
    redisTemplate.delete("vote" + voteId);

    try {
      scheduler.deleteJob(new JobKey("job" + voteEntity.getId(), "job"));
    } catch (SchedulerException e) {
      throw new RuntimeException(e);
    }
  }

  public List<VoteDto> getVotes(Long studyId) {
    return voteRepository.findByStudy_StudyIdAndStatus(studyId,
            PROGRESS).stream()
        .map(v -> VoteDto.fromEntity(v))
        .collect(Collectors.toList());
  }
}
