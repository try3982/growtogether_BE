package com.campfiredev.growtogether.study.repository;

import com.campfiredev.growtogether.study.entity.StudyComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyCommentRepository extends JpaRepository<StudyComment, Long> {
}
