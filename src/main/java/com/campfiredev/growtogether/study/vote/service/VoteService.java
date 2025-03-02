package com.campfiredev.growtogether.study.vote.service;

import static com.campfiredev.growtogether.exception.response.ErrorCode.*;
import static com.campfiredev.growtogether.study.type.StudyMemberType.KICK;
import static com.campfiredev.growtogether.study.type.StudyMemberType.LEADER;
import static com.campfiredev.growtogether.study.type.StudyMemberType.NORMAL;

import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.study.entity.join.StudyMemberEntity;
import com.campfiredev.growtogether.study.repository.join.JoinRepository;
import com.campfiredev.growtogether.study.vote.dto.KickVoteDto;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VoteService {

  private final JoinRepository joinRepository;
  private final RedisTemplate<String, Object> redisTemplate;

  /**
   * 강퇴 투표
   * @param memberId 로그인한 사용자 id
   * @param studyId 스터디 id
   * @param kickVoteDto 누구에게 투표했는지
   */
  public void kickVote(Long memberId, Long studyId, KickVoteDto kickVoteDto) {
    StudyMemberEntity studyMember = getStudyMemberEntity(memberId, studyId);
    String voteKey = generateVoteKey(studyMember.getId(), kickVoteDto.getStudyMemberId());

    validateVote(studyMember, voteKey);

    //해당 유저 득표 수
    Long count = incrementVote(voteKey, kickVoteDto.getStudyMemberId().toString());

    //현재 스터디 참가자 수(LEADER, NORMAL)
    int totalMembers = getStudyMemberCount(studyId);

    //득표 수가 과반수 이상이면 강퇴 처리
    if (count > totalMembers / 2) {
      StudyMemberEntity member = joinRepository.findById(kickVoteDto.getStudyMemberId())
          .orElseThrow(() -> new CustomException(NOT_A_STUDY_MEMBER));
      member.setStatus(KICK);
    }
  }

  private String generateVoteKey(Long fromId, Long toId) {
    return fromId + "to" + toId;
  }

  /**
   * redis에 누가 누구한테 투표했는지 저장,
   * 개개인 득표수 저장
   */
  private Long incrementVote(String voteKey, String targetMemberId) {
    String luaScript = getKickVoteLuaScript();

    DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
    redisScript.setScriptText(luaScript);
    redisScript.setResultType(Long.class);

    return redisTemplate.execute(
        redisScript,
        Arrays.asList("voteTo", "count"),
        voteKey,
        targetMemberId
    );
  }

  private int getStudyMemberCount(Long studyId) {
    return joinRepository.findByStudyWithMembersInStatus(studyId, List.of(NORMAL, LEADER)).size();
  }

  private String getKickVoteLuaScript() {
    return "redis.call('HINCRBY', KEYS[1], ARGV[1], 1); " +
        "local newCount = redis.call('HINCRBY', KEYS[2], ARGV[2], 1); " +
        "return newCount;";
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
}
