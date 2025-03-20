package com.campfiredev.growtogether.study.service.comment;

import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.repository.MemberRepository;
import com.campfiredev.growtogether.study.dto.comment.StudyCommentDto;
import com.campfiredev.growtogether.study.entity.StudyComment;
import com.campfiredev.growtogether.study.repository.StudyCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.campfiredev.growtogether.exception.response.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyCommentService {

    private final StudyCommentRepository studyCommentRepository;
    private final MemberRepository memberRepository;
    private final String deletedCommentMessage = "작성자에 의해 삭제된 댓글입니다.";

    public void createComment(StudyCommentDto dto, Long memberId) {
        MemberEntity member = memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );

        StudyComment comment = StudyComment.builder()
                .commentContent(dto.getCommentContent())
                .parentCommentId(dto.getParentCommentId())
                .studyId(dto.getStudyId())
                .member(member)
                .build();

        studyCommentRepository.save(comment);
    }

    public List<StudyCommentDto> getCommentsByStudyId(Long studyId, Long lastIdx, Long size) {
        Pageable pageable = PageRequest.of(0, size.intValue());

        Page<StudyComment> studyCommentList;

        if (0 == lastIdx) {
            studyCommentList = studyCommentRepository.findByStudyId(studyId, pageable);
        } else {
            studyCommentList = studyCommentRepository.findByStudyIdAndIdLessThan(studyId, lastIdx, pageable);
        }

        List<StudyCommentDto> studyCommentDtos = setChildComment(studyCommentList);
        return studyCommentDtos;
    }

    public List<StudyCommentDto> setChildComment(Page<StudyComment> studyCommentList) {
        return studyCommentList.stream()
                .map(comment ->
                        {
                            StudyCommentDto studyCommentDto = StudyCommentDto.fromEntity(comment);
                            List<StudyComment> childCommentList = studyCommentRepository.findByParentCommentIdOrderByCreatedAtDesc(comment.getStudyCommentId());
                            studyCommentDto.setChildComments(childCommentList.stream().map(StudyCommentDto::fromEntity).collect(Collectors.toList()));
                            return studyCommentDto;
                        }
                )
                .collect(Collectors.toList());
    }

    public void updateComment(Long commentId, StudyCommentDto dto, Long memberId) {
        StudyComment comment = studyCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

        validateMember(memberId, comment);

        comment.setCommentContent(dto.getCommentContent());
        studyCommentRepository.save(comment);
    }

    public void deleteComment(Long commentId, Long memberId) {
        StudyComment comment = studyCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

        validateMember(memberId, comment);

        comment.setCommentContent(deletedCommentMessage);
    }

    private void validateMember(Long memberId, StudyComment comment) {
        if (!comment.getMember().getMemberId().equals(memberId)) {
            throw new CustomException(NOT_AUTHOR);
        }
    }
}
