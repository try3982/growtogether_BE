package com.campfiredev.growtogether.point.service;

import com.campfiredev.growtogether.common.annotation.RedissonLock;
import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorCode;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

import static com.campfiredev.growtogether.exception.response.ErrorCode.INSUFFICIENT_POINTS;

@Service
@RequiredArgsConstructor
@Transactional
public class PointService {

    private final MemberRepository memberRepository;

    private final Map<String, LocalDate> lastLoginMap;

  //  @RedissonLock(key = "point:#{#memberId}", waitTime = 5, leaseTime = 10)
    public void usePoint(Long memberId, int amount) {
        MemberEntity MemberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (MemberEntity.getPoints() < amount) {
            throw new CustomException(INSUFFICIENT_POINTS);
        }

         MemberEntity.usePoints(amount);
    }

 //   @RedissonLock(key = "point:#{#memberId}", waitTime = 5, leaseTime = 10)
    public void updatePoint(MemberEntity memberEntity, int point) {
        LocalDate today = LocalDate.now();
        String email = memberEntity.getEmail();
        LocalDate lastLoginDate = lastLoginMap.getOrDefault(email, null);

        if (lastLoginDate != null && lastLoginDate.equals(today)) return;
        memberEntity.setPoints(memberEntity.getPoints() + point);
        lastLoginMap.put(email, today);
    }

}