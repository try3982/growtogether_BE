package com.campfiredev.growtogether.chat.controller;

import com.campfiredev.growtogether.chat.dto.ChatMessageDto;
import com.campfiredev.growtogether.chat.dto.SliceMessageDto;
import com.campfiredev.growtogether.chat.service.ChatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChatController {

  private final ChatService chatService;
  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;

  @MessageMapping("/study/{studyId}/send")
  @SendTo("/topic/study/{studyId}")
  public ChatMessageDto sendMessage(@DestinationVariable String studyId,
      @Payload ChatMessageDto chatMessageDto) {
    log.info(chatMessageDto.getMessage());

    chatMessageDto.setDate(LocalDateTime.now());

    try {
      redisTemplate.opsForList()
          .leftPush("chat" + studyId, objectMapper.writeValueAsString(chatMessageDto));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return chatMessageDto;
  }

  @MessageMapping("/study/{studyId}/enter")
  @SendTo("/topic/study/{studyId}")
  public ChatMessageDto enterChat(@DestinationVariable String studyId,
      @Payload ChatMessageDto chatMessageDto,
      @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {

    String username = (String) sessionAttributes.get("username");
    chatMessageDto.setMessage(username + "님이 입장하였습니다.");

    log.info(chatMessageDto.getMessage());

    return chatMessageDto;
  }

  @MessageMapping("/study/{studyId}/exit")
  @SendTo("/topic/study/{studyId}")
  public ChatMessageDto exitChat(@DestinationVariable String studyId,
      @Payload ChatMessageDto chatMessageDto) {
    chatMessageDto.setMessage(chatMessageDto.getSender() + "님이 퇴장하셨습니다.");
    chatMessageDto.setDate(LocalDateTime.now());

    log.info(chatMessageDto.getMessage());

    return chatMessageDto;
  }

  @GetMapping("/study/{studyId}/chat")
  public SliceMessageDto getChatMessages(@PathVariable Long studyId,
      @RequestParam(required = false) Integer lastIndex,
      @RequestParam(required = false) Integer size) {
    return chatService.getChatMessage(studyId, lastIndex, LocalDateTime.now(), size);
  }

}
