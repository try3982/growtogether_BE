package com.campfiredev.growtogether.chat.service;

import com.campfiredev.growtogether.chat.dto.ChatMessageDto;
import com.campfiredev.growtogether.chat.dto.SliceMessageDto;
import com.campfiredev.growtogether.chat.entity.ChatEntity;
import com.campfiredev.growtogether.chat.repository.ChatRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;
  private final ChatRepository chatRepository;

  public SliceMessageDto getChatMessage(Long studyId, Integer lastIndex, LocalDateTime date, int size){

    String key = "chat" + studyId;

    List<ChatMessageDto> chatMessages = new ArrayList<>();
    int start, end;

    if (lastIndex == null) {
      start = 0;
      end = size - 1;
    }
    else if (lastIndex > 0) {
      start = lastIndex;
      end = start + size - 1;
    }
    else {
      return fetchFromDB(studyId, lastIndex, size, date, chatMessages);
    }

    List<Object> range = redisTemplate.opsForList().range(key, start, end);
    if (range != null && !range.isEmpty()) {
      List<ChatMessageDto> collect = range.stream()
          .map(json -> jsonToChatMessageDto(json))
          .collect(Collectors.toList());

      chatMessages.addAll(collect);

      if (chatMessages.size() == size) {
        return SliceMessageDto.builder()
            .lastIndex(end + 1)
            .lastDate(collect.get(collect.size()-1).getDate())
            .chatMessages(chatMessages)
            .build();
      }
    }

    return fetchFromDB(studyId, null, size - chatMessages.size(), date , chatMessages);

  }

  private SliceMessageDto fetchFromDB(Long studyId, Integer lastIndex, int size, LocalDateTime date, List<ChatMessageDto> redisMessages) {
    LocalDateTime lastDate = LocalDateTime.now();
    System.out.println("size = " + size);

    if (!redisMessages.isEmpty()) {
      lastDate = redisMessages.get(redisMessages.size() - 1).getDate();
    } else {
      lastDate = date;
    }

    List<ChatEntity> dbMessages = new ArrayList<>();

    if(lastIndex == null || lastIndex > 0) {
      System.out.println("first");
      Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "date"));
      dbMessages = chatRepository
          .findByStudyIdAndDateBefore(studyId, lastDate, pageable);
    }else if(lastIndex < 0){
      System.out.println("second");
      Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
      dbMessages = chatRepository.findByStudyIdAndIdLessThanOrderByIdDesc(studyId,
          (long) -lastIndex, pageable);
    }

    System.out.println("dbMessages = " + dbMessages.size());

    List<ChatMessageDto> chatMessageDtos = dbMessages.stream()
        .map(chat -> ChatMessageDto.builder()
            .studyId(chat.getStudyId())
            .message(chat.getMessage())
            .sender(chat.getSender())
            .imageUrl(chat.getImageUrl())
            .date(chat.getDate()).build())
        .collect(Collectors.toList());

    redisMessages.addAll(chatMessageDtos);

    long lastPk = dbMessages.isEmpty() ? -1 : -dbMessages.get(dbMessages.size() - 1).getId();

    System.out.println("lastPk = " + lastPk);

    return SliceMessageDto.builder()
        .lastIndex((int) lastPk)
        .chatMessages(redisMessages)
        .lastDate(redisMessages.get(redisMessages.size()-1).getDate())
        .build();
  }

  private ChatMessageDto jsonToChatMessageDto(Object json) {
    try {
      return objectMapper.readValue(json.toString(), ChatMessageDto.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed", e);
    }
  }

}
