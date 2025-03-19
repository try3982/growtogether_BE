package com.campfiredev.growtogether.bootcamp.repository;


import com.campfiredev.growtogether.bootcamp.entity.BootCampComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BootCampCommentRepository extends JpaRepository<BootCampComment, Long> {

    boolean existsByBootCampCommentIdAndIsDeletedTrue(Long bootCampCommentId);

    @Query("SELECT DISTINCT c FROM BootCampComment c " +
            "LEFT JOIN FETCH c.childComments " +
            "LEFT JOIN FETCH c.member " +
            "WHERE c.bootCampReview.bootCampId =:bootCampId")
    List<BootCampComment> findCommentsWithChildrenByBootCampId(@Param("bootCampId") Long bootCampId);

    // 최초 요청 (부모 댓글만 가져오기)
    @Query("""
        SELECT c FROM BootCampComment c 
        LEFT JOIN FETCH c.member
        WHERE c.bootCampReview.bootCampId = :bootCampId 
        AND c.depth = 0 
        ORDER BY c.bootCampCommentId DESC
        """)
    List<BootCampComment> findParentComments(
            @Param("bootCampId") Long bootCampId,
            Pageable pageable
    );

    // lastIdx 이후 데이터만 가져오기 (무한 스크롤 요청)
    @Query("""
        SELECT c FROM BootCampComment c 
        LEFT JOIN FETCH c.member 
        WHERE c.bootCampReview.bootCampId = :bootCampId 
        AND c.bootCampCommentId < :lastIdx 
        AND c.depth = 0 
        ORDER BY c.bootCampCommentId DESC
        """)
    List<BootCampComment> findParentCommentsWithLastIdx(
            @Param("bootCampId") Long bootCampId,
            @Param("lastIdx") Long lastIdx,
            Pageable pageable
    );
}
