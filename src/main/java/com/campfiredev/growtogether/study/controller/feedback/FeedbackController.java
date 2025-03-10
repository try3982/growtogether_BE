package com.campfiredev.growtogether.study.controller.feedback;

import com.campfiredev.growtogether.study.service.feedback.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FeedbackController {

  private final FeedbackService feedbackService;

}
