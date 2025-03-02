package com.campfiredev.growtogether.study.vote;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VoteScheduler {

  private final RedisTemplate<String, Object> redisTemplate;

  @Scheduled(cron = "0 0 0 * * *")
  public void executeTask() {
    redisTemplate.delete("voteTo");
    redisTemplate.delete("count");
  }
}
