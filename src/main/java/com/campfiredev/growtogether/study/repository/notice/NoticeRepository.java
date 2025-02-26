package com.campfiredev.growtogether.study.repository.notice;

import com.campfiredev.growtogether.study.entity.notice.NoticeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<NoticeEntity, Long> {

  Page<NoticeEntity> findByStudyId(Long studyId, Pageable pageable);

}
