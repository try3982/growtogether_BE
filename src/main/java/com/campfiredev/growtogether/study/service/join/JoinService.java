package com.campfiredev.growtogether.study.service.join;

import static com.campfiredev.growtogether.exception.response.ErrorCode.ALREADY_CONFIRMED;
import static com.campfiredev.growtogether.exception.response.ErrorCode.ALREADY_JOINED_STUDY;
import static com.campfiredev.growtogether.exception.response.ErrorCode.STUDY_FULL;
import static com.campfiredev.growtogether.exception.response.ErrorCode.STUDY_NOT_FOUND;
import static com.campfiredev.growtogether.exception.response.ErrorCode.USER_NOT_APPLIED;
import static com.campfiredev.growtogether.exception.response.ErrorCode.USER_NOT_FOUND;
import static com.campfiredev.growtogether.study.entity.StudyStatus.COMPLETE;
import static com.campfiredev.growtogether.study.type.StudyMemberType.KICK;
import static com.campfiredev.growtogether.study.type.StudyMemberType.LEADER;
import static com.campfiredev.growtogether.study.type.StudyMemberType.NORMAL;
import static com.campfiredev.growtogether.study.type.StudyMemberType.PENDING;

import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.repository.MemberRepository;
import com.campfiredev.growtogether.point.service.PointService;
import com.campfiredev.growtogether.study.dto.join.StudyMemberListDto;
import com.campfiredev.growtogether.study.entity.Study;
import com.campfiredev.growtogether.study.entity.join.StudyMemberEntity;
import com.campfiredev.growtogether.study.repository.StudyRepository;
import com.campfiredev.growtogether.study.repository.join.JoinRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

  /**
   * 참가 신청
   *
   * @param memberId 로그인한 사용자 id
   * @param studyId  스터디 id
   */
  public void join(Long memberId, Long studyId) {
    MemberEntity memberEntity = memberRepository.findById(memberId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Study studyEntity = studyRepository.findById(studyId)
        .orElseThrow(() -> new CustomException(STUDY_NOT_FOUND));

    // 참가 조건 검증
    validateJoin(memberEntity, studyEntity);

    // 참가 정보 저장
    joinRepository.save(StudyMemberEntity.create(studyEntity, memberEntity));

    // 추후 참가 신청 메일 발송 로직 추가
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
   * 참여 신청자 리스트 조회
   * @param studyId 스터디 id
   * @return
   */
  public StudyMemberListDto getPendingList(Long studyId) {

    List<StudyMemberEntity> list = joinRepository.findByStudyWithMembersInStatus(studyId,
        List.of(PENDING));

    return StudyMemberListDto.fromEntity(list);
  }

  /**
   * 참여자 리스트 조회(팀장(LEADER), 일반 멤버(NORMAL), 강퇴자(KICK))
   * @param studyId
   * @return
   */
  public StudyMemberListDto getJoinList(Long studyId) {

    List<StudyMemberEntity> list = joinRepository.findByStudyWithMembersInStatus(studyId,
        List.of(NORMAL, LEADER, KICK));

    return StudyMemberListDto.fromEntity(list);
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
    if (COMPLETE.equals(studyEntity.getStudyStatus())) {
      throw new CustomException(STUDY_FULL);
    }

    joinRepository.findByMemberAndStudy(memberEntity, studyEntity)
        .ifPresent(studyMember -> {
          throw new CustomException(ALREADY_JOINED_STUDY);
        });
  }

}

