package com.campfiredev.growtogether.study.vote;

import com.campfiredev.growtogether.study.vote.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class KickVoteJob extends AbstractVoteJob {

  public KickVoteJob(VoteService voteService) {
    super(voteService);
  }

  @Override
  protected void executeJob(VoteService voteService, Long id) {
    voteService.sumKickVote(id);
  }
}
