package com.campfiredev.growtogether.study.service.notice;

import com.campfiredev.growtogether.study.dto.notice.NoticeCreateDto;
import com.campfiredev.growtogether.study.dto.notice.NoticeDetailsDto;
import com.campfiredev.growtogether.study.dto.notice.NoticeListDto;
import com.campfiredev.growtogether.study.dto.notice.NoticeUpdateDto;
import org.springframework.data.domain.Pageable;

public interface NoticeService {

  NoticeListDto getNotices(Long studyId, Pageable pageable);

  NoticeDetailsDto getNotice(Long noticeId);

  NoticeCreateDto.Response createNotice(Long studyId, NoticeCreateDto.Request request);

  NoticeUpdateDto.Response updateNotice(Long noticeId, NoticeUpdateDto.Request request);

  void deleteNotice(Long noticeId);
}
