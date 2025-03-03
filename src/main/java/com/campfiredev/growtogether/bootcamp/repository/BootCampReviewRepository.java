package com.campfiredev.growtogether.bootcamp.repository;

import com.campfiredev.growtogether.bootcamp.entity.BootCampReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BootCampReviewRepository extends JpaRepository<BootCampReview, Long> {

    Page<BootCampReview> findAll(Pageable pageable);

    @Query("SELECT DISTINCT b FROM BootCampReview b " +
            "LEFT JOIN FETCH b.bootCampSkills bs " +
            "LEFT JOIN FETCH bs.skill ")
    List<BootCampReview> findAllWithSkills(Pageable pageable);

    @Query("SELECT b FROM BootCampReview b "
    + "LEFT JOIN FETCH b.bootCampSkills bs "
    + "LEFT JOIN FETCH bs.skill s" +
    " WHERE b.bootCampId = :bootCampId")
    Optional<BootCampReview> findByIdWithSkills(@Param("bootCampId")Long bootCampId);

}