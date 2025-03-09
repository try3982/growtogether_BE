package com.campfiredev.growtogether.chat.service;

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

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    log.info("presend");
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String token = accessor.getFirstNativeHeader("Authorization");

      if (token == null || !token.startsWith("Bearer ")) {
        throw new IllegalArgumentException("JWT 토큰이 필요합니다.");
      }

      token = token.substring(7);

      log.info("jwt 검증 로직 추가할 예정");

      accessor.getSessionAttributes().put("username", "임시 닉네임");
    }

    return message;
  }
}
