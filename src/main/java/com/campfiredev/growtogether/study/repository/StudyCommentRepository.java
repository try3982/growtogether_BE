package com.campfiredev.growtogether.study.repository;

import com.campfiredev.growtogether.study.entity.StudyComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyCommentRepository extends JpaRepository<StudyComment, Long> {
    List<StudyComment> findByStudyId(Long studyId);

    Optional<StudyComment> findByStudyCommentIdAndMember_Email(long id, String email);
    Integer countAllByStudyId(Long studyId);
}
