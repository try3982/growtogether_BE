package com.campfiredev.growtogether.point.service;

import com.campfiredev.growtogether.common.annotation.RedissonLock;
import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorCode;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.repository.MemberRepository;
import com.campfiredev.growtogether.point.dto.PointHistoryResponseDto;
import com.campfiredev.growtogether.point.entity.PointTransaction;
import com.campfiredev.growtogether.point.repository.PointTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.campfiredev.growtogether.exception.response.ErrorCode.INSUFFICIENT_POINTS;

@Service
@RequiredArgsConstructor
public class PointService {

    private final MemberRepository memberRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final Map<String, LocalDate> lastLoginMap;

    // 포인트 사용
    public void usePoint(Long memberId, int amount) {
        MemberEntity member = memberRepository.findByIdWithLock(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (member.getPoints() < amount) {
            throw new CustomException(INSUFFICIENT_POINTS);
        }

        // 포인트 차감
        member.setPoints(member.getPoints() - amount);
        memberRepository.save(member);

        // 포인트 거래 내역 저장
        PointTransaction transaction = PointTransaction.builder()
                .member(member)
                .date(LocalDateTime.now())
                .type(PointTransaction.TransactionType.USE)
                .amount(amount)
                .build();
        pointTransactionRepository.save(transaction);
    }

    // 포인트 적립
    public void addPoint(Long memberId, int amount, PointTransaction.TransactionType type) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 포인트 추가
        member.setPoints(member.getPoints() + amount);
        memberRepository.save(member);

        // 포인트 거래 내역 저장
        PointTransaction transaction = PointTransaction.builder()
                .member(member)
                .date(LocalDateTime.now())
                .type(type)
                .amount(amount)
                .build();
        pointTransactionRepository.save(transaction);
    }

    //  로그인 시 포인트 추가
    public void updatePoint(MemberEntity memberEntity, int point) {
        LocalDate today = LocalDate.now();
        String email = memberEntity.getEmail();
        LocalDate lastLoginDate = lastLoginMap.getOrDefault(email, null);

        if (lastLoginDate != null && lastLoginDate.equals(today)) return;

        memberEntity.setPoints(memberEntity.getPoints() + point);
        memberRepository.save(memberEntity);
        lastLoginMap.put(email, today);
    }

    // 포인트 내역 조회
    public PointHistoryResponseDto getPointHistory(Long memberId) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 현재 사용 가능한 포인트
        int availablePoints = member.getPoints();

        // 포인트 거래 내역 조회
        List<PointHistoryResponseDto.PointHistoryItem> history = pointTransactionRepository.findByMemberOrderByDateDesc(member)
                .stream()
                .map(tx -> new PointHistoryResponseDto.PointHistoryItem(
                        tx.getDate().toLocalDate().toString(), // 날짜 형식 변환
                        tx.getType().name(),
                        (tx.getType() == PointTransaction.TransactionType.USE ? "-" : "+") + tx.getAmount()
                ))
                .collect(Collectors.toList());

        return new PointHistoryResponseDto(availablePoints, history);
    }
}
