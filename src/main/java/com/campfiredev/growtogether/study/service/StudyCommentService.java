package com.campfiredev.growtogether.study.service;

import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.study.dto.StudyCommentDto;
import com.campfiredev.growtogether.study.entity.StudyComment;
import com.campfiredev.growtogether.study.repository.StudyCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.campfiredev.growtogether.exception.response.ErrorCode.COMMENT_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyCommentService {

    private final StudyCommentRepository studyCommentRepository;

    public StudyCommentDto createComment(StudyCommentDto dto) {

        StudyComment comment = StudyComment.builder()
                .commentContent(dto.getCommentContent())
                .parentCommentId(dto.getParentCommentId())
                .studyId(dto.getStudyId())
                .build();

        return StudyCommentDto.fromEntity(studyCommentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<StudyCommentDto> getCommentsByStudyId(Long studyId) {
        return studyCommentRepository.findByStudyId(studyId).stream()
                .map(StudyCommentDto::fromEntity)
                .toList();
    }

    @Transactional
    public StudyCommentDto updateComment(Long commentId, StudyCommentDto dto) {
        StudyComment comment = studyCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

        comment.setCommentContent(dto.getCommentContent());
        return StudyCommentDto.fromEntity(studyCommentRepository.save(comment));
    }
}
