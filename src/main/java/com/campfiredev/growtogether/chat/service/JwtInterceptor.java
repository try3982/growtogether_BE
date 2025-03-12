package com.campfiredev.growtogether.chat.service;

import com.campfiredev.growtogether.member.util.JwtUtil;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtInterceptor implements ChannelInterceptor {

  private final JwtUtil jwtUtil;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    log.info("presend");
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String token = accessor.getFirstNativeHeader("Authorization");
      String studyId = accessor.getFirstNativeHeader("studyId");
      String username = accessor.getFirstNativeHeader("username");

      if (token == null || !token.startsWith("Bearer ")) {
        throw new IllegalArgumentException("JWT 토큰이 필요합니다.");
      }

      token = token.substring(7);

      jwtUtil.isTokenValid(token);

      log.info("jwt 검증 로직 추가할 예정");

      System.out.println(studyId);
      Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
      System.out.println(sessionAttributes);

      accessor.getSessionAttributes().put("studyId", studyId);

      System.out.println(sessionAttributes);
    }

    return message;
  }
}
