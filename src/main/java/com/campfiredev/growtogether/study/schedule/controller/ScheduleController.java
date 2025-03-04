package com.campfiredev.growtogether.study.schedule.controller;

import com.campfiredev.growtogether.study.schedule.dto.ScheduleCreateDto;
import com.campfiredev.growtogether.study.schedule.dto.ScheduleDto;
import com.campfiredev.growtogether.study.schedule.dto.ScheduleMonthDto;
import com.campfiredev.growtogether.study.schedule.dto.ScheduleUpdateDto;
import com.campfiredev.growtogether.study.schedule.service.ScheduleService;
import jakarta.ejb.Schedule;
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

  @PostMapping("/{studyId}/schedule")
  public void createSchedule(@PathVariable Long studyId,
      @RequestBody @Valid ScheduleCreateDto scheduleCreateDto) {
    scheduleService.createSchedule(studyId, 1L, scheduleCreateDto);
  }

  @PutMapping("/schedule/{scheduleId}")
  public void updateSchedule(@PathVariable Long scheduleId,
      @RequestBody @Valid ScheduleUpdateDto scheduleUpdateDto) {
    scheduleService.updateSchedule(1L, scheduleId, scheduleUpdateDto);
  }

  @DeleteMapping("/schedule/{scheduleId}")
  public void deleteSchedule(@PathVariable Long scheduleId) {
    scheduleService.deleteSchedule(1L, scheduleId);
  }

  @GetMapping("/{studyId}/schedule")
  public ResponseEntity<List<ScheduleDto>> getSchedule(@PathVariable Long studyId,
      @RequestParam(required = false) LocalDate date) {
    if (date == null) {
      date = LocalDate.now();
    }
    return ResponseEntity.ok(scheduleService.getSchedules(studyId, date));
  }

  @GetMapping("/{studyId}/schedules")
  public ResponseEntity<ScheduleMonthDto> getSchedules(@PathVariable Long studyId,
      @RequestParam(required = false) String date) {
    if (date == null) {
      date = String.valueOf(YearMonth.now());
    }
    return ResponseEntity.ok(scheduleService.getMonthSchedules(studyId, date));
  }
}
