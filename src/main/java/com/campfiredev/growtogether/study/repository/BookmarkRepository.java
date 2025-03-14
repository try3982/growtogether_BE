package com.campfiredev.growtogether.study.repository;

import com.campfiredev.growtogether.study.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByMember_MemberIdAndStudy_StudyId(Long userId, Long studyId);

 // // 사용자가 좋아요한 스터디 개수
    Long countByMember_MemberId(Long memberId);
}

