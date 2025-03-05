package com.campfiredev.growtogether.point.service;

import static com.campfiredev.growtogether.exception.response.ErrorCode.*;

//import com.campfiredev.growtogether.common.annotation.RedissonLock;
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

      @RedissonLock(key = "point:#{#memberId}", waitTime = 5, leaseTime = 10)
    public void usePoint(Long memberId, int amount) {
        MemberEntity MemberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (MemberEntity.getPoints() < amount) {
            throw new CustomException(INSUFFICIENT_POINTS);
        }

        // MemberEntity.usePoints(amount);
    }

    @RedissonLock(key = "point:#{#memberId}", waitTime = 5, leaseTime = 10)
    public void updatePoint(Long memberId, int point) {
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        memberEntity.setPoints(memberEntity.getPoints() + point);
    }

}
