package com.campfiredev.growtogether.notification.controller;


import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorCode;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.repository.MemberRepository;
import com.campfiredev.growtogether.notification.service.NotificationService;
import com.campfiredev.growtogether.notification.type.NotiType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
public class SseNotiContoller {

    private final NotificationService notificationService;
    private final MemberRepository memberRepository;


    @GetMapping(value = "/subscribe/{userId}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long userId, HttpServletResponse response){
        try {
            response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*"); // 모든 오리진 허용
            response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Type");
            //cors 문제 발생으로 인한 헤더 설정 추가
            return notificationService.subscribe(userId);
        } catch (Exception e) {
            log.error("SSE 구독 중 오류 발생 : {} ", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Headers - Key : Accept Value: text/event-stream
     * @param id
     */
    @PostMapping("/send-data/{id}")
    public void sendData(@PathVariable Long id){
        String msg = "Noti test sendData " + id;

        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        notificationService.sendNotification(member,msg,"https://www.naver.com/",NotiType.BOOTCAMP);
    }


}
