package com.campfiredev.growtogether.chat;

import com.campfiredev.growtogether.chat.dto.ChatMessageDto;
import com.campfiredev.growtogether.chat.entity.ChatEntity;
import com.campfiredev.growtogether.chat.repository.ChatRepository;
import com.campfiredev.growtogether.study.entity.Study;
import com.campfiredev.growtogether.study.entity.join.StudyMemberEntity;
import com.campfiredev.growtogether.study.repository.join.JoinRepository;
import com.campfiredev.growtogether.study.repository.post.StudyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.campfiredev.growtogether.study.entity.StudyStatus.PROGRESS;
import static com.campfiredev.growtogether.study.type.StudyMemberType.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageScheduler {

  private final RedisTemplate<String, String> redisTemplate;
  private final ObjectMapper objectMapper;
  private final ChatRepository chatMessageRepository;
  private final StudyRepository studyRepository;
  private final JoinRepository joinRepository;

  @Scheduled(cron = "0 0 0 * * *")
  public void persistOldMessages() {

    List<Study> studies = studyRepository.findByStudyStatus(PROGRESS);

    log.info("chat scheduler");

    for(Study study : studies) {
      String redisKey = "chat" + study.getStudyId();

      List<String> oldMessages = redisTemplate.opsForList().range(redisKey, 100, -1);

      List<ChatMessageDto> messagesToSave = oldMessages.stream().map(msg -> {
        try {
          return objectMapper.readValue(msg, ChatMessageDto.class);
        } catch (Exception e) {
          log.error("Failed: {}", msg, e);
          return null;
        }
      }).filter(msg -> msg != null).collect(Collectors.toList());

      messagesToSave.sort(Comparator.comparing(ChatMessageDto::getDate));

      List<StudyMemberEntity> findStudyMembers = joinRepository.findByStudyWithMembersInStatus(
          study.getStudyId(), List.of(NORMAL, LEADER, KICK));

      Map<Long, StudyMemberEntity> studyMemberMap = findStudyMembers.stream()
          .collect(Collectors.toMap(sm -> sm.getId(), Function.identity()));

      List<ChatEntity> collect = messagesToSave.stream()
          .map(a -> ChatEntity.builder()
              .study(study)
              .message(a.getMessage())
              .date(a.getDate())
              .sender(studyMemberMap.get(a.getStudyMemberId()))
              .imageUrl(a.getImageUrl()).build())
          .collect(Collectors.toList());

      chatMessageRepository.saveAll(collect);

      redisTemplate.opsForList().trim(redisKey, 0, 2);
    }
  }
}
