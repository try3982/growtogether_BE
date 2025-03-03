package com.campfiredev.growtogether.study.controller;

import com.campfiredev.growtogether.study.dto.StudyCommentDto;
import com.campfiredev.growtogether.study.service.StudyCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study/comments")
public class StudyCommentController {

    private final StudyCommentService studyCommentService;

    @PostMapping
    public StudyCommentDto createComment(@Valid @RequestBody StudyCommentDto dto) {
        return studyCommentService.createComment(dto);
    }

    @GetMapping("/{studyId}")
    public List<StudyCommentDto> getComments(@PathVariable Long studyId) {
        return studyCommentService.getCommentsByStudyId(studyId);
    }

    @PutMapping("/{commentId}")
    public StudyCommentDto updateComment(@PathVariable Long commentId, @Valid @RequestBody StudyCommentDto dto) {
        return studyCommentService.updateComment(commentId, dto);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        studyCommentService.deleteComment(commentId);
    }
}
