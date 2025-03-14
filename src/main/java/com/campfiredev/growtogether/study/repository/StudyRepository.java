package com.campfiredev.growtogether.study.repository;

import com.campfiredev.growtogether.study.entity.Study;
import com.campfiredev.growtogether.study.entity.StudyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {
    Page<Study> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    List<Study> findByStudyStatus(StudyStatus studyStatus);
}

