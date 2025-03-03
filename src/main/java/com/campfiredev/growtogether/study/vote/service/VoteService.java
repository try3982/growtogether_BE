package com.campfiredev.growtogether.study.vote.service;

import static com.campfiredev.growtogether.exception.response.ErrorCode.*;
import static com.campfiredev.growtogether.study.type.StudyMemberType.KICK;
import static com.campfiredev.growtogether.study.type.StudyMemberType.LEADER;
import static com.campfiredev.growtogether.study.type.StudyMemberType.NORMAL;
import static com.campfiredev.growtogether.study.vote.type.VoteStatus.COMPLETE;
import static com.campfiredev.growtogether.study.vote.type.VoteStatus.PROGRESS;

import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorCode;
import com.campfiredev.growtogether.study.entity.join.StudyMemberEntity;
import com.campfiredev.growtogether.study.repository.join.JoinRepository;
import com.campfiredev.growtogether.study.vote.ChangeVoteJob;
import com.campfiredev.growtogether.study.vote.KickVoteJob;
import com.campfiredev.growtogether.study.vote.SchedulerService;
import com.campfiredev.growtogether.study.vote.dto.UpdateScheduleDto;
import com.campfiredev.growtogether.study.vote.dto.VotingDto;
import com.campfiredev.growtogether.study.vote.dto.VoteCreateDto;
import com.campfiredev.growtogether.study.vote.dto.VoteDto;
import com.campfiredev.growtogether.study.vote.entity.ChangeVoteEntity;
import com.campfiredev.growtogether.study.vote.entity.KickVoteEntity;
import com.campfiredev.growtogether.study.vote.entity.VoteEntity;
import com.campfiredev.growtogether.study.vote.entity.VotingEntity;
import com.campfiredev.growtogether.study.vote.repository.ChangeVoteRepository;
import com.campfiredev.growtogether.study.vote.repository.KickVoteRepository;
import com.campfiredev.growtogether.study.vote.repository.VoteRepository;
import com.campfiredev.growtogether.study.vote.repository.VotingRepository;
import com.campfiredev.growtogether.study.vote.type.VoteType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
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

  public void createKickVote(Long memberId, Long studyId, VoteCreateDto voteCreateDto) {

    StudyMemberEntity studyMemberEntity = getStudyMemberEntity(memberId, studyId);

    StudyMemberEntity voted = joinRepository.findById(voteCreateDto.getStudyMemberId())
        .orElseThrow(() -> new CustomException(NOT_A_STUDY_MEMBER));

    String title = voted.getMember().getNickName() + "님의 대한 추방 투표입니다.";

    KickVoteEntity save = kickVoteRepository.save(KickVoteEntity.builder()
        .title(title)
        .studyMember(studyMemberEntity)
        .study(studyMemberEntity.getStudy())
        .status(PROGRESS)
        .target(voted)
        .build());

    scheduleJob(KickVoteJob.class, "kickJob", "kickGroup", 3, save.getId());
  }

  public void createChangeVote(Long memberId, Long studyId, Long scheduleId, UpdateScheduleDto updateScheduleDto){
    StudyMemberEntity studyMemberEntity = getStudyMemberEntity(memberId, studyId);

    String title = scheduleId + "번 스케줄 시간 변경 투표입니다.";

    ChangeVoteEntity save = changeVoteRepository.save(ChangeVoteEntity.builder()
        .title(title)
        .studyMember(studyMemberEntity)
        .study(studyMemberEntity.getStudy())
        .status(PROGRESS)
        .content(updateScheduleDto.getContent())
        .date(updateScheduleDto.getDate())
        .time(updateScheduleDto.getTime())
        .build());

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

    //validateVote(voteEntity, studyMemberEntity);

    votingRepository.save(VotingEntity.create(voteEntity, studyMemberEntity));

    saveInRedis(voteId, votingDto);

  }


  private StudyMemberEntity getStudyMemberEntity(Long memberId, Long studyId) {
    return joinRepository.findByMemberIdAndStudyId(memberId, studyId)
        .orElseThrow(() -> new CustomException(NOT_A_STUDY_MEMBER));
  }

  private void validateVote(StudyMemberEntity studyMemberEntity, String hash) {
    if (!LEADER.equals(studyMemberEntity.getStatus()) && !NORMAL.equals(
        studyMemberEntity.getStatus())) {
      throw new CustomException(STUDY_MEMBER_ONLY);
    }

    if(redisTemplate.opsForHash().hasKey("voteTo", hash)){
      throw new CustomException(VOTING_ALREADY_EXISTS);
    }
  }

    private void saveInRedis(Long voteId, VotingDto votingDto) {
      String key = "vote" + voteId;

      if(votingDto.isFlag()) {
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

    int size = find.size();

    Map<Object, Object> entries = redisTemplate.opsForHash().entries("vote" + voteId);

    Object totalVotes = entries.get("total");

    System.out.println("📊 총 투표 수: " + (totalVotes != null ? totalVotes : 0));

    // ✅ 참가자별 득표 수 출력
    for (Map.Entry<Object, Object> entry : entries.entrySet()) {
      String field = entry.getKey().toString();
      String value = entry.getValue().toString();

      System.out.println("size : " + size + "value : " + value + " field : " + field);

      // ✅ 'totalVotes' 필드는 제외하고 참가자 정보만 출력
      if (!field.equals("total")) {
        System.out.println("🗳 참가자 " + field + " 득표 수: " + value);
        if (Integer.parseInt(value) > size / 2) {
          KickVoteEntity kickVoteEntity = (KickVoteEntity) voteEntity;
          StudyMemberEntity studyMemberEntity = joinRepository.findById(kickVoteEntity.getTarget().getId())
              .orElseThrow(() -> new CustomException(ErrorCode.NOT_A_STUDY_MEMBER));

          studyMemberEntity.setStatus(KICK);
          System.out.println("after");
        }
      }

    }
      voteEntity.setStatus(COMPLETE);
      redisTemplate.delete("vote" + voteId);
  }

  public void sumChangeVote(Long voteId) {
    VoteEntity voteEntity = voteRepository.findVoteAndStudyById(voteId)
        .orElseThrow(() -> new CustomException(ErrorCode.VOTE_NOT_FOUND));

    List<StudyMemberEntity> find = joinRepository.findByStudyWithMembersInStatus(
        voteEntity.getStudy().getStudyId(),
        List.of(NORMAL, LEADER));

    int size = find.size();

    Map<Object, Object> entries = redisTemplate.opsForHash().entries("vote" + voteId);

    Object totalVotes = entries.get("total");

    System.out.println("📊 총 투표 수: " + (totalVotes != null ? totalVotes : 0));

    // ✅ 참가자별 득표 수 출력
    for (Map.Entry<Object, Object> entry : entries.entrySet()) {
      String field = entry.getKey().toString();
      String value = entry.getValue().toString();

      System.out.println("size : " + size + "value : " + value + " field : " + field);

      // ✅ 'totalVotes' 필드는 제외하고 참가자 정보만 출력
      if (!field.equals("total")) {
        System.out.println("🗳 참가자 " + field + " 득표 수: " + value);
        if (Integer.parseInt(value) >= size) {

          ChangeVoteEntity changeVoteEntity = (ChangeVoteEntity) voteEntity;
          System.out.println(changeVoteEntity.getDate());
          System.out.println(changeVoteEntity.getTime());
          System.out.println("after");
        }
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
