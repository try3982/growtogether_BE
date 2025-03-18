package com.campfiredev.growtogether.bootcamp.repository;


import com.campfiredev.growtogether.bootcamp.entity.BootCampComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BootCampCommentRepository extends JpaRepository<BootCampComment, Long> {

    //@Query("select c from BootCampComment c LEFT JOIN FETCH c.childComments where c.bootCampReview = :review AND c.parentComment IS NULL")
   // Page<BootCampComment> findTopLevelCommentsWithChildren(@Param("review") BootCampReview review, Pageable pageable);

    boolean existsByBootCampCommentIdAndIsDeletedTrue(Long bootCampCommentId);

    @Query("SELECT DISTINCT c FROM BootCampComment c " +
            "LEFT JOIN FETCH c.childComments " +
            "LEFT JOIN FETCH c.member " +
            "WHERE c.bootCampReview.bootCampId =:bootCampId")
    List<BootCampComment> findCommentsWithChildrenByBootCampId(@Param("bootCampId") Long bootCampId);
}
