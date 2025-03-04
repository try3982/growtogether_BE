package com.campfiredev.growtogether.study.vote.service;

import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorCode;
import com.campfiredev.growtogether.study.vote.entity.ChangeVoteEntity;
import com.campfiredev.growtogether.study.vote.entity.KickVoteEntity;
import com.campfiredev.growtogether.study.vote.entity.VoteEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VoteProcessorFactory {

  private final Map<Class<? extends VoteEntity>, VoteProcessor> processors = new HashMap<>();

  @Autowired
  public VoteProcessorFactory(List<VoteProcessor> processorList) {
    for (VoteProcessor processor : processorList) {
      if (processor instanceof KickVoteProcessor) {
        processors.put(KickVoteEntity.class, processor);
      } else if (processor instanceof ChangeVoteProcessor) {
        processors.put(ChangeVoteEntity.class, processor);
      }
    }
  }

  public VoteProcessor getProcessor(Class<? extends VoteEntity> voteClass) {
    return processors.getOrDefault(voteClass, (a,b,c) -> {
      throw new CustomException(ErrorCode.VOTE_NOT_FOUND);
    });
  }
}
