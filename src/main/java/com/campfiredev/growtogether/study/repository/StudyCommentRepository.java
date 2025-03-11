package com.campfiredev.growtogether.study.repository;

import com.campfiredev.growtogether.study.entity.StudyComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyCommentRepository extends JpaRepository<StudyComment, Long> {
    List<StudyComment> findByStudyId(Long studyId);
    Integer countAllByStudyId(Long studyId);
}
