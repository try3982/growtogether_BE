package com.campfiredev.growtogether.chat;

import com.campfiredev.growtogether.chat.dto.ChatMessageDto;
import com.campfiredev.growtogether.chat.entity.ChatEntity;
import com.campfiredev.growtogether.chat.repository.ChatRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageScheduler {

  private final RedisTemplate<String, String> redisTemplate;
  private final ObjectMapper objectMapper;
  private final ChatRepository chatMessageRepository;

  @Scheduled(fixedRate = 60000)
  public void persistOldMessages() {

      String redisKey = "chat" + 1;

      List<String> oldMessages = redisTemplate.opsForList().range(redisKey, 3, -1);

      List<ChatMessageDto> messagesToSave = oldMessages.stream().map(msg -> {
        try {
          return objectMapper.readValue(msg, ChatMessageDto.class);
        } catch (Exception e) {
          log.error("Failed: {}", msg, e);
          return null;
        }
      }).filter(msg -> msg != null).collect(Collectors.toList());

    messagesToSave.sort(Comparator.comparing(ChatMessageDto::getDate));


    List<ChatEntity> collect = messagesToSave.stream()
        .map(a -> ChatEntity.builder()
            .studyId(a.getStudyId())
            .message(a.getMessage())
            .date(a.getDate())
            .sender(a.getSender())
            .imageUrl(a.getImageUrl()).build())
        .collect(Collectors.toList());

      chatMessageRepository.saveAll(collect);

      redisTemplate.opsForList().trim(redisKey, 0, 2);

  }
}
