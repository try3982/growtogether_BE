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
  STUDY_MEMBER_ONLY("참가 중인 사람만 투표할 수 있습니다.", BAD_REQUEST),
  VOTING_ALREADY_EXISTS("이미 투표하셨습니다.", BAD_REQUEST),
  VOTE_ALREADY_COMPLETE("이미 종료된 투표입니다.",BAD_REQUEST),
  SCHEDULE_NOT_FOUND("존재하지 않는 일정입니다.",BAD_REQUEST),
  NOT_AUTHOR("작성자가 아닙니다.",BAD_REQUEST),
  CANNOT_DELETE_MAIN_SCHEDULE("메인 일정은 삭제할 수 없습니다.", BAD_REQUEST),
  ALREADY_EXISTS_SCHEDULE("겹치는 일정이 존재합니다.",BAD_REQUEST),
  INVALID_ATTENDANCE_TIME("출석 가능 시간이 아닙니다.", BAD_REQUEST),
  ALREADY_ATTENDANCE("이미 출석했습니다.",BAD_REQUEST),
  ALREADY_FEEDBACK("이미 피드백을 했습니다.", BAD_REQUEST),
  INVALID_FEEDBACK_PERIOD("피드백 기간이 아닙니다.", BAD_REQUEST),


  // 파일 업로드 관련 예외
  FILE_UPLOAD_FAILED("파일 업로드에 실패했습니다.", HttpStatus.BAD_REQUEST),
  FILE_SIZE_EXCEEDED("파일 크기 제한을 초과했습니다.", HttpStatus.PAYLOAD_TOO_LARGE),
  UNSUPPORTED_FILE_TYPE("지원되지 않는 파일 형식입니다.", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
  FILE_STORAGE_ERROR("파일 저장 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

  //jwt exception
  NOT_VALID_TOKEN("토큰이 유효하지 않습니다.", UNAUTHORIZED),
  EXPIRED_TOKEN("토큰이 만료되었습니다.", UNAUTHORIZED),


  ALREADY_DELETED_STUDY("이미 삭제된 게시글 입니다.",BAD_REQUEST),

  ALREADY_JOINED_STUDY("이미 참석 중인 스터디입니다.", BAD_REQUEST),
  NOT_A_STUDY_MEMBER("스터디 참가자가 아닙니다.", BAD_REQUEST),
  NOT_A_STUDY_LEADER("스터디 팀장이 아닙니다.", BAD_REQUEST),

  VOTE_NOT_FOUND("존재하지 않는 투표입니다.", BAD_REQUEST),

  NOTICE_NOT_FOUND("존재하지 않는 공지사항입니다.",BAD_REQUEST),

  INVALID_INPUT_DATA("잘못된 입력 데이터입니다.", BAD_REQUEST),

  INTERNAL_SERVER_ERROR("서버 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),

  INVALID_SKILL("존재하지 않는 기술스택입니다.",BAD_REQUEST),

  REVIEW_NOT_FOUND("존재하지 않는 후기입니다.",BAD_REQUEST),

  COMMENT_ACCESS_DENIED("해당 댓글에 접근 권한이 없습니다.",BAD_REQUEST),

  NOTI_NOT_FOUND("해당 알림을 찾을 수 없습니다.",BAD_REQUEST),
  COMMENT_DEPTH_EXCEED("댓글 뎁스 초과입니다.",BAD_REQUEST);

  private final String description;

  private final HttpStatus status;
}
