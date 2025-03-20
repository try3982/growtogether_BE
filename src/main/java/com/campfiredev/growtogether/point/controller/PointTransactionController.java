package com.campfiredev.growtogether.point.controller;

import com.campfiredev.growtogether.point.dto.PointHistoryResponseDto;
import com.campfiredev.growtogether.point.entity.PointTransaction;
import com.campfiredev.growtogether.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointTransactionController {

    private final PointService pointService;

    //  포인트 내역 조회 API
    @GetMapping("/history")
    public ResponseEntity<PointHistoryResponseDto> getPointHistory(@RequestParam Long memberId) {
        return ResponseEntity.ok(pointService.getPointHistory(memberId));
    }

    // 포인트 사용 API
    @PostMapping("/use")
    public ResponseEntity<String> usePoint(@RequestParam Long memberId, @RequestParam int amount) {
        pointService.usePoint(memberId, amount);
        return ResponseEntity.ok("포인트 사용 성공");
    }

    // 포인트 적립 API
    @PostMapping("/add")
    public ResponseEntity<String> addPoint(@RequestParam Long memberId, @RequestParam int amount) {
        pointService.addPoint(memberId, amount, PointTransaction.TransactionType.REWARD);
        return ResponseEntity.ok("포인트 적립 성공");
    }

    //  포인트 충전 API
    @PostMapping("/charge")
    public ResponseEntity<String> chargePoint(@RequestParam Long memberId, @RequestParam int amount) {
        pointService.addPoint(memberId, amount, PointTransaction.TransactionType.CHARGE);
        return ResponseEntity.ok("포인트 충전 성공");
    }
}
