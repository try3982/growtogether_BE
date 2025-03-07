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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VoteService {

  private final JoinRepository joinRepository;
  private final VoteRepository voteRepository;
  private final SchedulerService schedulerService;
  private final VotingRepository votingRepository;
  private final KickVoteRepository kickVoteRepository;
  private final ChangeVoteRepository changeVoteRepository;
  private final RedisTemplate<String, Object> redisTemplate;
  private final VoteProcessorFactory voteProcessorFactory;

  public void createKickVote(Long memberId, Long studyId, VoteCreateDto voteCreateDto) {

    StudyMemberEntity studyMemberEntity = getStudyMemberEntity(memberId, studyId);

    StudyMemberEntity voted = joinRepository.findById(voteCreateDto.getStudyMemberId())
        .orElseThrow(() -> new CustomException(NOT_A_STUDY_MEMBER));

    String title = voted.getMember().getNickName() + "님의 대한 추방 투표입니다.";

    KickVoteEntity save = kickVoteRepository.save(
        KickVoteEntity.create(title, studyMemberEntity, voted));

    scheduleJob(KickVoteJob.class, "kickJob", "kickGroup", 3, save.getId());
  }

  public void createChangeVote(Long memberId, Long studyId, Long scheduleId,
      ScheduleUpdateDto scheduleUpdateDto) {
    StudyMemberEntity studyMemberEntity = getStudyMemberEntity(memberId, studyId);

    String title = scheduleId + "번 스케줄 시간 변경 투표입니다.";

    ChangeVoteEntity save = changeVoteRepository.save(
        ChangeVoteEntity.create(title, studyMemberEntity, scheduleUpdateDto.getTitle(),
            scheduleUpdateDto.getDate(), scheduleUpdateDto.getTime(), scheduleId));

    scheduleJob(ChangeVoteJob.class, "changeJob", "changeGroup", 3, save.getId());
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

    saveInRedis(voteId, votingDto);
  }

  private StudyMemberEntity getStudyMemberEntity(Long memberId, Long studyId) {
    return joinRepository.findByMemberIdAndStudyIdInStatus(memberId, studyId,
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

  private void saveInRedis(Long voteId, VotingDto votingDto) {
    String key = "vote" + voteId;

    if (votingDto.isAgree()) {
      redisTemplate.opsForHash().increment(key, "agree", 1);
    }
    redisTemplate.opsForHash().increment(key, "total", 1);
  }

  public void sumKickVote(Long voteId) {
    VoteEntity voteEntity = voteRepository.findVoteAndStudyById(voteId)
        .orElseThrow(() -> new CustomException(ErrorCode.VOTE_NOT_FOUND));

    List<StudyMemberEntity> find = joinRepository.findByStudyWithMembersInStatus(
        voteEntity.getStudy().getStudyId(),
        List.of(NORMAL, LEADER));

    Map<Object, Object> entries = redisTemplate.opsForHash().entries("vote" + voteId);

    VoteProcessor processor = voteProcessorFactory.getProcessor(voteEntity.getClass());

    for (Map.Entry<Object, Object> entry : entries.entrySet()) {
      String field = entry.getKey().toString();
      String value = entry.getValue().toString();

      if (field != null && !field.equals("total")) {
        processor.processVote(voteEntity, Integer.parseInt(value), find.size());
      }
    }
    voteEntity.setStatus(COMPLETE);
    redisTemplate.delete("vote" + voteId);
  }

  public List<VoteDto> getVotes(Long studyId) {
    return voteRepository.findVoteInProgressByStudyId(studyId,
            PROGRESS).stream()
        .map(v -> VoteDto.fromEntity(v))
        .collect(Collectors.toList());
  }
}
