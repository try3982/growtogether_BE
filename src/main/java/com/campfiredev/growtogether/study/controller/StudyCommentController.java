package com.campfiredev.growtogether.study.controller;

import com.campfiredev.growtogether.study.dto.StudyCommentDto;
import com.campfiredev.growtogether.study.service.StudyCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study/comments")
public class StudyCommentController {

    private final StudyCommentService studyCommentService;

    @PostMapping
    public void createComment(@Valid @RequestBody StudyCommentDto dto, @AuthenticationPrincipal String email) {
        studyCommentService.createComment(dto,email);
    }

    @GetMapping("/{studyId}")
    public List<StudyCommentDto> getComments(@PathVariable Long studyId) {
        return studyCommentService.getCommentsByStudyId(studyId);
    }

    @PutMapping("/{commentId}")
    public StudyCommentDto updateComment(@PathVariable Long commentId, @Valid @RequestBody StudyCommentDto dto, @AuthenticationPrincipal String email) {
        return studyCommentService.updateComment(commentId, dto, email);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal String email) {
        studyCommentService.deleteComment(commentId,email);
    }
}
