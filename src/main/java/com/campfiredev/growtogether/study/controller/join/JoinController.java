package com.campfiredev.growtogether.study.controller.join;

import com.campfiredev.growtogether.study.service.join.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
   * @param id 스터디 id
   */
  @PostMapping("{id}/join")
  public void join(@PathVariable Long id) {
    joinService.join(8L,id);
  }

  /**
   * 스터디 참가 확정
   * 로그인 구현 이후
   * @AuthenticationPrincipal로 사용자 정보 가져와 넘길 예정
   * @param id 스터디멤버 id
   */
  @PutMapping("/join/{id}")
  public void confirmJoin(@PathVariable Long id) {
    joinService.confirmJoin(id);
  }

  /**
   * 스터디 참가 신청 취소
   * 로그인 구현 이후
   * @AuthenticationPrincipal로 사용자 정보 가져와 넘길 예정
   * @param id 스터디멤버 id
   */
  @DeleteMapping("/join/{id}")
  public void cancelJoin(@PathVariable Long id) {
    joinService.cancelJoin(id);
  }
}

