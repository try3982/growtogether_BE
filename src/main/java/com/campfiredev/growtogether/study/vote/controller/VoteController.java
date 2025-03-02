package com.campfiredev.growtogether.study.vote.controller;

import com.campfiredev.growtogether.study.vote.dto.KickVoteDto;
import com.campfiredev.growtogether.study.vote.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study")
public class VoteController {

  private final VoteService voteService;

  /**
   * 강퇴 투표
   * 로그인 구현 이후
   * @AuthenticationPrincipal로 사용자 정보 가져와 넘길 예정
   */
  @PostMapping("/{studyId}/kick_vote")
  public void kickVote(@PathVariable Long studyId, @RequestBody @Valid KickVoteDto kickVoteDto){
    voteService.kickVote(3L, studyId, kickVoteDto);
  }

}
