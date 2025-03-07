package com.campfiredev.growtogether.study.controller.schedule;

import com.campfiredev.growtogether.study.dto.schedule.ScheduleCreateDto;
import com.campfiredev.growtogether.study.dto.schedule.ScheduleDto;
import com.campfiredev.growtogether.study.dto.schedule.ScheduleMonthDto;
import com.campfiredev.growtogether.study.dto.schedule.ScheduleUpdateDto;
import com.campfiredev.growtogether.study.service.schedule.ScheduleService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study")
public class ScheduleController {

  private final ScheduleService scheduleService;

  /**
   * 일정 추가
   * 로그인 이후 사용자 id도 넘길 예정
   */
  @PostMapping("/{studyId}/schedule")
  public void createSchedule(@PathVariable Long studyId,
      @RequestBody @Valid ScheduleCreateDto scheduleCreateDto) {
    scheduleService.createSchedule(studyId, 1L, scheduleCreateDto);
  }

  /**
   * 일정 수정
   * 로그인 이후 사용자 id도 넘길 예정
   */
  @PutMapping("/schedule/{scheduleId}")
  public void updateSchedule(@PathVariable Long scheduleId,
      @RequestBody @Valid ScheduleUpdateDto scheduleUpdateDto) {
    scheduleService.updateSchedule(1L, scheduleId, scheduleUpdateDto);
  }

  /**
   * 일정 삭제
   * 로그인 이후 사용자 id도 넘길 예정
   */
  @DeleteMapping("/schedule/{scheduleId}")
  public void deleteSchedule(@PathVariable Long scheduleId) {
    scheduleService.deleteSchedule(1L, scheduleId);
  }

  /**
   * 해당일의 일정 리스트 조회
   */
  @GetMapping("/{studyId}/schedule")
  public ResponseEntity<List<ScheduleDto>> getSchedule(@PathVariable Long studyId,
      @RequestParam(required = false) LocalDate date) {
    if (date == null) {
      date = LocalDate.now();
    }
    return ResponseEntity.ok(scheduleService.getSchedules(studyId, date));
  }

  /**
   * 해당 달의 일정 리스트 조회
   */
  @GetMapping("/{studyId}/schedules")
  public ResponseEntity<ScheduleMonthDto> getSchedules(@PathVariable Long studyId,
      @RequestParam(required = false) String date) {
    if (date == null) {
      date = String.valueOf(YearMonth.now());
    }
    return ResponseEntity.ok(scheduleService.getMonthSchedules(studyId, date));
  }
}
