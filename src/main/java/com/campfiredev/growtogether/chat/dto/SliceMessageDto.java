package com.campfiredev.growtogether.chat.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SliceMessageDto {

  private Integer lastIndex;

  private List<ChatMessageDto> chatMessages;
}
