package com.campfiredev.growtogether.study.vote.controller;

import com.campfiredev.growtogether.study.vote.dto.UpdateScheduleDto;
import com.campfiredev.growtogether.study.vote.dto.VotingDto;
import com.campfiredev.growtogether.study.vote.dto.VoteCreateDto;
import com.campfiredev.growtogether.study.vote.dto.VoteDto;
import com.campfiredev.growtogether.study.vote.service.VoteService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

//  /**
//   * 강퇴 투표
//   * 로그인 구현 이후
//   * @AuthenticationPrincipal로 사용자 정보 가져와 넘길 예정
//   */
//  @PostMapping("/{studyId}/kick_vote")
//  public void kickVote(@PathVariable Long studyId, @RequestBody @Valid VotingDto votingDto){
//    voteService.kickVote(3L, studyId, votingDto);
//  }

  @PostMapping("/vote/{voteId}")
  public void vote(@PathVariable Long voteId, @RequestBody @Valid VotingDto votingDto) {
    voteService.vote(2L, voteId, votingDto);
  }

  @PostMapping("/{studyId}/vote")
  public void createKickVote(@PathVariable Long studyId,
      @RequestBody @Valid VoteCreateDto voteCreateDto) {
    voteService.createKickVote(1L, studyId, voteCreateDto);
  }

  @GetMapping("/{studyId}/vote")
  public ResponseEntity<List<VoteDto>> getPollsInProgress(@PathVariable Long studyId) {
    return ResponseEntity.ok(voteService.getVotes(studyId));
  }

  @PostMapping("/{studyId}/change_vote")
  public void createChangeVote(@PathVariable Long studyId,
      @RequestBody @Valid UpdateScheduleDto updateScheduleDto) {
    voteService.createChangeVote(1L, studyId, 1L, updateScheduleDto);
  }

}
