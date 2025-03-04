package com.campfiredev.growtogether.study.vote.service;

import com.campfiredev.growtogether.study.vote.entity.VoteEntity;

public interface VoteProcessor {
  void processVote(VoteEntity voteEntity, int votes, int totalSize);
}
