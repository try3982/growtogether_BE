package com.campfiredev.growtogether.study.controller.feedback;

import com.campfiredev.growtogether.study.dto.feedback.FeedbackCreateDto;
import com.campfiredev.growtogether.study.dto.feedback.FeedbackRequestDto;
import com.campfiredev.growtogether.study.service.feedback.FeedbackService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study")
public class FeedbackController {

  private final FeedbackService feedbackService;

  @PostMapping("/{studyId}/feedback")
  public void feedback(@PathVariable Long studyId, @RequestBody @Valid FeedbackRequestDto feedbackRequestDto){
    feedbackService.feedback(2L,studyId,feedbackRequestDto.getFeedbacks());
  }

}
