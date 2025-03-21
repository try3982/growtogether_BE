package com.campfiredev.growtogether.study.repository.post;

import com.campfiredev.growtogether.study.dto.post.StudyFilter;
import com.campfiredev.growtogether.study.entity.Study;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudyRepositoryCustom {
    Page<Study> findFilteredAndSortedStudies(StudyFilter filter, Pageable pageable);
}
