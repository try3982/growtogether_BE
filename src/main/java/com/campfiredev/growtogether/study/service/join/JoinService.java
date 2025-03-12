package com.campfiredev.growtogether.study.service.join;

import static com.campfiredev.growtogether.exception.response.ErrorCode.ALREADY_CONFIRMED;
import static com.campfiredev.growtogether.exception.response.ErrorCode.ALREADY_JOINED_STUDY;
import static com.campfiredev.growtogether.exception.response.ErrorCode.STUDY_FULL;
import static com.campfiredev.growtogether.exception.response.ErrorCode.STUDY_NOT_FOUND;
import static com.campfiredev.growtogether.exception.response.ErrorCode.USER_NOT_APPLIED;
import static com.campfiredev.growtogether.exception.response.ErrorCode.USER_NOT_FOUND;
import static com.campfiredev.growtogether.notification.type.NotiType.STUDY;
import static com.campfiredev.growtogether.study.entity.StudyStatus.COMPLETE;
import static com.campfiredev.growtogether.study.entity.StudyStatus.RECRUIT;
import static com.campfiredev.growtogether.study.type.StudyMemberType.KICK;
import static com.campfiredev.growtogether.study.type.StudyMemberType.LEADER;
import static com.campfiredev.growtogether.study.type.StudyMemberType.NORMAL;
import static com.campfiredev.growtogether.study.type.StudyMemberType.PENDING;

import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.repository.MemberRepository;
import com.campfiredev.growtogether.notification.service.NotificationService;
import com.campfiredev.growtogether.point.service.PointService;
import com.campfiredev.growtogether.study.dto.join.JoinCreateDto;
import com.campfiredev.growtogether.study.dto.join.JoinDetailsDto;
import com.campfiredev.growtogether.study.dto.join.StudyMemberListDto;
import com.campfiredev.growtogether.study.entity.Study;
import com.campfiredev.growtogether.study.entity.join.StudyMemberEntity;
import com.campfiredev.growtogether.study.repository.StudyRepository;
import com.campfiredev.growtogether.study.repository.join.JoinRepository;
import com.campfiredev.growtogether.study.type.StudyMemberType;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JoinService {

  private final JoinRepository joinRepository;
  private final MemberRepository memberRepository;
  private final StudyRepository studyRepository;
  private final PointService pointService;
  private final RedisTemplate<String, Object> redisTemplate;
  private final NotificationService notificationService;

  /**
   * 참가 신청
   *
   * @param memberId 로그인한 사용자 id
   * @param studyId  스터디 id
   */
  public void join(Long memberId, Long studyId, JoinCreateDto joinCreateDto) {
    MemberEntity memberEntity = memberRepository.findById(memberId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Study studyEntity = studyRepository.findById(studyId)
        .orElseThrow(() -> new CustomException(STUDY_NOT_FOUND));

    // 참가 조건 검증
    validateJoin(memberEntity, studyEntity);

    // 참가 정보 저장
    StudyMemberEntity save = joinRepository.save(
        StudyMemberEntity.create(studyEntity, memberEntity));

    redisTemplate.opsForValue()
        .set("join" + save.getId(), joinCreateDto.getContent(), 7, TimeUnit.DAYS);

    // 추후 참가 신청 메일 발송 로직 추가

    List<StudyMemberEntity> find = joinRepository.findByStudyWithMembersInStatus(
        studyId, List.of(LEADER));

    StudyMemberEntity studyMemberEntity = find.get(0);

    notificationService.sendNotification(studyMemberEntity.getMember(),"참가 신청함", null,STUDY);
  }

  /**
   * 참가 신청 확정 로그인 구현 이후 현재 로그인 한 사용자 정보도 받을 예정
   *
   * @param studyMemberId
   */
  public void confirmJoin(Long studyMemberId) {
    StudyMemberEntity studyMemberEntity = joinRepository.findWithStudyAndMemberById(studyMemberId)
        .orElseThrow(() -> new CustomException(USER_NOT_APPLIED));

    validateConfirmJoin(studyMemberEntity);

    studyMemberEntity.confirm();

    /**
     * 포인트 확인 후 차감
     * 동시성 이슈로 인해 redisson lock 적용
     */
    pointService.usePoint(studyMemberEntity.getMember().getMemberId(),
        studyMemberEntity.getStudy().getStudyCount() * 5);
  }

  /**
   * 참가 신청 취소 로그인 구현 이후 현재 로그인 한 사용자 정보도 받을 예정
   *
   * @param studyMemberId
   */
  public void cancelJoin(Long studyMemberId) {
    StudyMemberEntity studyMemberEntity = joinRepository.findWithStudyAndMemberById(studyMemberId)
        .orElseThrow(() -> new CustomException(USER_NOT_APPLIED));

    //validateCancelJoin(studyMemberEntity);

    joinRepository.deleteById(studyMemberId);
  }

  /**
   * 참가신청 세부사항 조히
   */
  public JoinDetailsDto getJoin(Long studyMemberId){
    StudyMemberEntity studyMemberEntity = joinRepository.findWithSkillsById(studyMemberId)
        .orElseThrow(() -> new CustomException(USER_NOT_APPLIED));

    String content = (String) redisTemplate.opsForValue().get("join" + studyMemberId);

    return JoinDetailsDto.from(studyMemberEntity, content);
  }

  /**
   * 참여 신청자 리스트 조회
   *
   * @param studyId 스터디 id
   * @return
   * 원하는 타입의 참여자 리스트 조회
   * 팀장(LEADER), 일반 멤버(NORMAL), 참여 대기자(PENDING), 강퇴자(KICK)
   */
  public List<StudyMemberListDto> getStudyMember(Long studyId, List<StudyMemberType> types) {

    List<StudyMemberEntity> studyMemberList = joinRepository.findByStudyWithMembersInStatus(studyId,
        types);

    return getStudyMemberListDtos(studyMemberList);
  }

  /**
   * 피드백용 참여자 리스트 조회
   * 자기 자신 제외
   */
  public List<StudyMemberListDto> getStudyMemberForFeedback(Long studyId, Long memberId) {

    List<StudyMemberEntity> list = joinRepository.findByStudyWithMembersInStatus(studyId,
        List.of(LEADER, NORMAL, KICK));

    List<StudyMemberEntity> studyMemberList = list.stream()
        .filter(studyMember -> !studyMember.getMember().getMemberId().equals(memberId))
        .collect(Collectors.toList());

    return getStudyMemberListDtos(studyMemberList);
  }

  /**
   * 로그인 구현 이후 현재 로그인한 사용자 정보 넘길 예정
   *
   * @param studyMemberEntity
   */
  private void validateCancelJoin(StudyMemberEntity studyMemberEntity) {
    if (!PENDING.equals(studyMemberEntity.getStatus())) {
      throw new CustomException(ALREADY_CONFIRMED);
    }

    //로그인 완성될때까지 보류
    //스터디 팀장 혹은 신청자 본인만 취소 가능하도록 하는거 추가
  }

  /**
   * 로그인 구현 이후 현재 로그인한 사용자 정보 넘길 예정
   *
   * @param studyMemberEntity
   */
  private void validateConfirmJoin(StudyMemberEntity studyMemberEntity) {
    if (!PENDING.equals(studyMemberEntity.getStatus())) {
      throw new CustomException(ALREADY_CONFIRMED);
    }

    //로그인 완성될때까지 보류
    //현재 로그인 한 사용자 정보 가져와서 해당 스터디 팀장인지 확인
//    StudyMemberEntity leader = joinRepository.findByUserIdAndStudyId(userId, studyMemberEntity.getStudy().getId())
//        .orElseThrow(() -> new CustomException(NOT_A_STUDY_MEMBER));
//
//    if(!LEADER.equals(leader.getStatus())){
//      throw new CustomException(NOT_A_STUDY_LEADER);
//    }
  }

  /**
   * 참가 신청 validation
   *
   * @param memberEntity
   * @param studyEntity
   */
  private void validateJoin(MemberEntity memberEntity, Study studyEntity) {
    if (RECRUIT.equals(studyEntity.getStudyStatus())) {
      throw new CustomException(STUDY_FULL);
    }

    joinRepository.findByMemberAndStudy(memberEntity, studyEntity)
        .ifPresent(studyMember -> {
          throw new CustomException(ALREADY_JOINED_STUDY);
        });
  }

  private static List<StudyMemberListDto> getStudyMemberListDtos(List<StudyMemberEntity> studyMemberList) {
    return studyMemberList.stream()
        .map(studyMember -> StudyMemberListDto.fromEntity(studyMember))
        .collect(Collectors.toList());
  }

}

