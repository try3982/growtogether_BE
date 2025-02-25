package com.campfiredev.growtogether.study.controller.notice;

import com.campfiredev.growtogether.study.dto.notice.NoticeCreateDto;
import com.campfiredev.growtogether.study.dto.notice.NoticeDetailsDto;
import com.campfiredev.growtogether.study.dto.notice.NoticeListDto;
import com.campfiredev.growtogether.study.dto.notice.NoticeUpdateDto;
import com.campfiredev.growtogether.study.service.notice.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study")
public class NoticeController {

  private final NoticeService noticeService;

  /**
   * 공지 리스트 조회
   * @param id 스더디 id
   * @param pageable pageable 객체
   * @return NoticeListDto
   */
  @GetMapping("{id}/notice")
  public ResponseEntity<NoticeListDto> getNoticesPage(@PathVariable Long id, Pageable pageable) {
    return ResponseEntity.ok(noticeService.getNotices(id, pageable));
  }

  /**
   * 공지 세부 조회
   * @param id 공지사항 id
   * @return NoticeDetailsDto
   */
  @GetMapping("/notice/{id}")
  public ResponseEntity<NoticeDetailsDto> getNoticeDetails(@PathVariable Long id) {
    return ResponseEntity.ok(noticeService.getNotice(id));
  }

  /**
   * 공지 생성
   * 로그인 개발 후 @AuthenticationPrincipal로 사용자 정보 가져와 createNotice에 넘길 예정
   */
  @PostMapping("{id}/notice")
  public ResponseEntity<NoticeCreateDto.Response> addNotice(@PathVariable Long id,
      @RequestBody @Valid NoticeCreateDto.Request request) {
    return ResponseEntity.ok(noticeService.createNotice(id, request));
  }

  /**
   * 공지 수정
   * 로그인 개발 후 @AuthenticationPrincipal로 사용자 정보 가져와 updateNotice에 넘길 예정
   */
  @PutMapping("/notice/{id}")
  public ResponseEntity<NoticeUpdateDto.Response> updateNotice(@PathVariable Long id,
      @RequestBody @Valid NoticeUpdateDto.Request request) {
    return ResponseEntity.ok(noticeService.updateNotice(id, request));
  }

  /**
   * 공지 삭제
   * 로그인 개발 후 @AuthenticationPrincipal로 사용자 정보 가져와 deleteNotice에 넘길 예정
   */
  @DeleteMapping("/notice/{id}")
  public void deleteNotice(@PathVariable Long id) {
    noticeService.deleteNotice(id);
  }
}
