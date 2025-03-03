package com.campfiredev.growtogether.study.vote;

import com.campfiredev.growtogether.study.vote.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution  // 같은 Job이 동시에 실행되지 않도록 설정
@RequiredArgsConstructor
public class ChangeVoteJob implements Job {

  private final VoteService voteService;

  @Override
  public void execute(JobExecutionContext context) {
    System.out.println("changeVoteJob");
    Long id = context.getJobDetail().getJobDataMap().getLong("id");

    voteService.sumChangeVote(id);

  }
}
