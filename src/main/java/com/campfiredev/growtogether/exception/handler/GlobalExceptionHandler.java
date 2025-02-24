package com.campfiredev.growtogether.exception.handler;

import static com.campfiredev.growtogether.exception.response.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.campfiredev.growtogether.exception.response.ErrorCode.INVALID_INPUT_DATA;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * CustomException 처리 핸들러
   * @param e 발생한 CustomException
   * @return ErrorResponse
   */
  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
    return ResponseEntity.status(e.getStatus()).body(new ErrorResponse(e.getErrorCode()));
  }

  /**
   * @Valid 유효성 검사 실패 (MethodArgumentNotValidException) 처리 핸들러
   * @param e 발생한 MethodArgumentNotValidException
   * @return ErrorResponse (잘못된 입력 데이터 관련 에러 메시지 포함)
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {

    String description = e.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .findFirst().orElse(INVALID_INPUT_DATA.getDescription());

    return ResponseEntity.status(BAD_REQUEST)
        .body(new ErrorResponse(INVALID_INPUT_DATA, description));
  }

  /**
   * 예상하지 못한 예외(Exception) 처리 핸들러
   * @param e 발생한 예외
   * @return ErrorResponse
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    return ResponseEntity.status(INTERNAL_SERVER_ERROR.getStatus()).body(new ErrorResponse(INTERNAL_SERVER_ERROR));
  }

}
