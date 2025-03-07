package com.campfiredev.growtogether.study.repository.notice;

import com.campfiredev.growtogether.study.entity.notice.NoticeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoticeRepository extends JpaRepository<NoticeEntity, Long> {

  @Query("select n from NoticeEntity n where n.study.studyId = :studyId")
  Page<NoticeEntity> findByStudyId(@Param("studyId") Long studyId, Pageable pageable);

}
