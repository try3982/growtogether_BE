package com.campfiredev.growtogether.study.controller.join;

import com.campfiredev.growtogether.study.dto.join.StudyMemberListDto;
import com.campfiredev.growtogether.study.service.join.JoinService;
import com.campfiredev.growtogether.study.type.StudyMemberType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study")
public class JoinController {

  private final JoinService joinService;

  /**
   * 스터디 참가 신청
   * 로그인 구현 이후
   * @AuthenticationPrincipal로 사용자 정보 가져와 넘길 예정
   * @param studyId 스터디 id
   */
  @PostMapping("{studyId}/join")
  public void join(@PathVariable Long studyId) {
    joinService.join(3L,studyId);
  }

  /**
   * 스터디 참가 확정
   * 로그인 구현 이후
   * @AuthenticationPrincipal로 사용자 정보 가져와 넘길 예정
   * @param joinId 스터디멤버 id
   */
  @PutMapping("/join/{joinId}")
  public void confirmJoin(@PathVariable Long joinId) {
    joinService.confirmJoin(joinId);
  }

  /**
   * 스터디 참가 신청 취소
   * 로그인 구현 이후
   * @AuthenticationPrincipal로 사용자 정보 가져와 넘길 예정
   * @param joinId 스터디멤버 id
   */
  @DeleteMapping("/join/{joinId}")
  public void cancelJoin(@PathVariable Long joinId) {
    joinService.cancelJoin(joinId);
  }

  /**
   * 스터디 참가자 리스트 조회
   * @param studyId 스터디 id
   * @param types 참가자 타입
   * @return
   */
  @GetMapping("/{studyId}/studyMember")
  public List<StudyMemberListDto> studyMemberList(@PathVariable Long studyId, @RequestBody List<StudyMemberType> types) {
    return joinService.getStudyMember(studyId, types);
  }

  /**
   * 피드백용 스터디 참가자 리스트 조회
   * 로그인 구현 이후
   * @AuthenticationPrincipal로 사용자 정보 가져와 넘길 예정
   */
  @GetMapping("/{studyId}/studyMember_feedback")
  public List<StudyMemberListDto> studyMemberListFeedback(@PathVariable Long studyId) {
    return joinService.getStudyMemberForFeedback(studyId, 1L);
  }
}

