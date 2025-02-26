package com.campfiredev.growtogether.study.repository;

import com.campfiredev.growtogether.study.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Long> {
}

