package com.campfiredev.growtogether.exception.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  //예외 생길 때마다 이런 식으로 추가
  NOT_INVALID_MEMBER("유효하지 않은 사용자입니다.", UNAUTHORIZED),
  USER_NOT_FOUND("해당 유저가 존재하지 않습니다.",BAD_REQUEST),
  INSUFFICIENT_POINTS("포인트가 부족합니다.",BAD_REQUEST),
  STUDY_NOT_FOUND("존재하지 않는 스터디입니다.",BAD_REQUEST),
  COMMENT_NOT_FOUND("존재하지 않는 댓글입니다.",BAD_REQUEST),
  USER_NOT_APPLIED("참가 신청 중인 유저가 아닙니다.",BAD_REQUEST),
  ALREADY_CONFIRMED("이미 참가 완료 된 유저입니다.",BAD_REQUEST),
  STUDY_FULL("모집 완료된 스터디입니다.",BAD_REQUEST),

  ALREADY_DELETED_STUDY("이미 삭제된 게시글 입니다.",BAD_REQUEST),

  ALREADY_JOINED_STUDY("이미 참석 중인 스터디입니다.", BAD_REQUEST),
  NOT_A_STUDY_MEMBER("스터디 참가자가 아닙니다.", BAD_REQUEST),
  NOT_A_STUDY_LEADER("스터디 팀장이 아닙니다.", BAD_REQUEST),

  NOTICE_NOT_FOUND("존재하지 않는 공지사항입니다.",BAD_REQUEST),

  INVALID_INPUT_DATA("잘못된 입력 데이터입니다.", BAD_REQUEST),

  INTERNAL_SERVER_ERROR("서버 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),

  INVALID_SKILL("존재하지 않는 기술스택입니다.",BAD_REQUEST),

  START_DATE_PAST("시작 날짜는 현재 날짜 이후여야 합니다.",BAD_REQUEST),

  END_DATE_AFTER_START_DATE("종료 날짜는 시작 날짜 이후여야 합니다.",BAD_REQUEST),

  REVIEW_NOT_FOUND("존재하지 않는 후기입니다.",BAD_REQUEST);

  private final String description;

  private final HttpStatus status;
}

