package com.campfiredev.growtogether.study.repository;

import com.campfiredev.growtogether.study.entity.StudyComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyCommentRepository extends JpaRepository<StudyComment, Long> {
    List<StudyComment> findByStudyIdAndStudyCommentIdLessThanOrderByStudyCommentIdDesc(Long studyId, Long lastIdx, Pageable pageable);
    List<StudyComment> findByStudyIdOrderByStudyCommentIdDesc(Long studyId, Pageable pageable);
    Integer countAllByStudyId(Long studyId);
}
