package com.campfiredev.growtogether.study.repository;

import com.campfiredev.growtogether.study.entity.Study;
import com.campfiredev.growtogether.study.entity.StudyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {
    List<Study> findByIsDeletedFalseOrderByCreatedAtDesc();

    List<Study> findByStudyStatus(StudyStatus studyStatus);
}

