package com.campfiredev.growtogether.point.service;


import static com.amazonaws.services.kms.model.ConnectionErrorCodeType.USER_NOT_FOUND;
import static com.campfiredev.growtogether.exception.response.ErrorCode.*;

import com.campfiredev.growtogether.common.annotation.RedissonLock;
import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorCode;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PointService {

  private final MemberRepository memberRepository;

  @RedissonLock(key = "point:#{#userId}", waitTime = 5, leaseTime = 10)
  public void usePoint(Long userId, int amount){
    MemberEntity memberEntity = memberRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    if(memberEntity.getPoints() < amount){
      throw new CustomException(INSUFFICIENT_POINTS);
    }

    memberEntity.usePoints(amount);
  }

}

