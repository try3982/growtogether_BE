package com.campfiredev.growtogether.bootcamp.repository;

import com.campfiredev.growtogether.bootcamp.entity.BootCampReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BootCampReviewRepository extends JpaRepository<BootCampReview, Long> {

    Page<BootCampReview> findAll(Pageable pageable);
}