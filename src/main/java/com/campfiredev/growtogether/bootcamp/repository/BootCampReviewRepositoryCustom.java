package com.campfiredev.growtogether.bootcamp.repository;

import com.campfiredev.growtogether.bootcamp.entity.BootCampReview;
import com.campfiredev.growtogether.bootcamp.strategy.WeightCalculateStrategy;
import com.campfiredev.growtogether.bootcamp.type.ProgramCourse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BootCampReviewRepositoryCustom {
    Page<BootCampReview> searchBootCamps(String bootCampName ,String title,ProgramCourse programCourse , String skillName, Pageable pageable);

    List<BootCampReview> findTopRankedReviews(WeightCalculateStrategy strategy, int limit);
}
