package com.campfiredev.growtogether.chat.service;

import com.campfiredev.growtogether.chat.dto.ChatMessageDto;
import com.campfiredev.growtogether.chat.dto.SliceMessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;

  public SliceMessageDto getChatMessage(Long studyId, Integer lastIndex, int size){

    String key = "chat" + studyId;

    if(lastIndex == null){
      int start = 0;
      int end = size -1;

      List<Object> range = redisTemplate.opsForList().range(key, start, end);

      return SliceMessageDto.builder()
          .lastIndex(end + 1)
          .chatMessages(range.stream()
              .map(obj -> jsonToChatMessageDto(obj))
              .collect(Collectors.toList()))
          .build();

    }

    int start = lastIndex;
    int end = start + size -1;

    List<Object> range = redisTemplate.opsForList().range(key, start, end);

    return SliceMessageDto.builder()
        .lastIndex(end + 1)
        .chatMessages(range.stream()
            .map(obj -> jsonToChatMessageDto(obj))
            .collect(Collectors.toList()))
        .build();

  }

  private ChatMessageDto jsonToChatMessageDto(Object json) {
    try {
      return objectMapper.readValue(json.toString(), ChatMessageDto.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to deserialize ChatMessageDto", e);
    }
  }

}
