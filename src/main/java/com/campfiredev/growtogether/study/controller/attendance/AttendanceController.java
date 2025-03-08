package com.campfiredev.growtogether.study.controller.attendance;

import com.campfiredev.growtogether.study.dto.attendance.AttendanceDto;
import com.campfiredev.growtogether.study.service.attendance.AttendanceService;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study")
public class AttendanceController {

  private final AttendanceService attendanceService;

  /**
   * 로그인 구현되면 사용자 정보 넘길 예정
   * @param studyId
   */
  @PostMapping("/{studyId}/attendance")
  public void attendance(@PathVariable Long studyId){
    attendanceService.attendance(1L, studyId);
  }

  @GetMapping("/{studyId}/attendance")
  public List<AttendanceDto> getAttendees(@PathVariable Long studyId, @RequestParam(required = false) String date){
    if(date == null){
      date = String.valueOf(YearMonth.now());
    }
    return attendanceService.getAttendee(studyId, date);
  }

}
