package com.campfiredev.growtogether.notification.controller;

import com.campfiredev.growtogether.notification.dto.NotificationDto;
import com.campfiredev.growtogether.notification.dto.NotificationRequestDto;
import com.campfiredev.growtogether.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/noti")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 사용자의 읽지 않는 알림 목록 조회
     * @param request
     * @return 읽지 않는 알림 리스트
     */
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications(@RequestBody NotificationRequestDto request){

        List<NotificationDto> notificationDtos = notificationService.getUnReadNotifiactions(request.getMemberId());

        return ResponseEntity.ok(notificationDtos);
    }

    /**
     * 특정 알림 읽음 처리
     * @param notiId
     * @return
     */
    @PutMapping("{notiId}/read")
    public ResponseEntity<String> markNotiRead(@PathVariable Long notiId){
        notificationService.markNotification(notiId);

        return ResponseEntity.ok("해당 알림을 읽으셨습니다.");
    }

}
