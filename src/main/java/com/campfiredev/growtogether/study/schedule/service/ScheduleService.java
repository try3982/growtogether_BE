package com.campfiredev.growtogether.study.schedule.service;

import static com.campfiredev.growtogether.exception.response.ErrorCode.*;
import static com.campfiredev.growtogether.study.schedule.type.ScheduleType.MAIN;
import static com.campfiredev.growtogether.study.type.StudyMemberType.LEADER;
import static com.campfiredev.growtogether.study.type.StudyMemberType.NORMAL;

import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorCode;
import com.campfiredev.growtogether.study.entity.Study;
import com.campfiredev.growtogether.study.entity.join.StudyMemberEntity;
import com.campfiredev.growtogether.study.repository.join.JoinRepository;
import com.campfiredev.growtogether.study.schedule.dto.MainScheduleDto;
import com.campfiredev.growtogether.study.schedule.dto.ScheduleCreateDto;
import com.campfiredev.growtogether.study.schedule.dto.ScheduleDto;
import com.campfiredev.growtogether.study.schedule.dto.ScheduleMonthDto;
import com.campfiredev.growtogether.study.schedule.dto.ScheduleUpdateDto;
import com.campfiredev.growtogether.study.schedule.entity.ScheduleEntity;
import com.campfiredev.growtogether.study.schedule.repository.ScheduleRepository;
import com.campfiredev.growtogether.study.vote.service.VoteService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

  private final ScheduleRepository scheduleRepository;
  private final JoinRepository joinRepository;
  private final VoteService voteService;

  public void createMainSchedule(Study study, Long memberId, List<MainScheduleDto> mainList) {
    mainList.sort(Comparator.comparing(s -> s.getStartTime()));

    for (int i = 1; i < mainList.size(); i++) {
      MainScheduleDto prev = mainList.get(i - 1);
      MainScheduleDto current = mainList.get(i);

      if (!current.getStartTime().isAfter(prev.getStartTime())) {
        throw new CustomException(ALREADY_EXISTS_SCHEDULE);
      }
    }

    StudyMemberEntity studyMemberEntity = getStudyMemberEntity(study.getStudyId(), memberId);

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    List<ScheduleEntity> schedules = mainList.stream()
        .map(main -> ScheduleEntity.builder()
            .title("메인 일정입니다.")
            .studyMember(studyMemberEntity)
            .study(study)
            .date(LocalDate.parse(main.getStartTime().format(dateFormatter)))
            .time(LocalTime.parse(main.getStartTime().format(timeFormatter)))
            .type(MAIN)
            .build())
        .collect(Collectors.toList());

    scheduleRepository.saveAll(schedules);
  }

  public void createSchedule(Long studyId, Long memberId, ScheduleCreateDto scheduleCreateDto) {
    StudyMemberEntity studyMemberEntity = getStudyMemberEntity(studyId, memberId);

    scheduleRepository.save(ScheduleEntity.create(studyMemberEntity, scheduleCreateDto.getTitle(),
        scheduleCreateDto.getDate(), scheduleCreateDto.getTime()));
  }

  public void updateSchedule(Long memberId, Long scheduleId, ScheduleUpdateDto scheduleUpdateDto) {
    ScheduleEntity scheduleEntity = getScheduleEntity(scheduleId);

    validateCreateVote(memberId, scheduleId, scheduleUpdateDto, scheduleEntity);

    validateSameUser(memberId, scheduleEntity);

    scheduleEntity.setTitle(scheduleUpdateDto.getTitle());
    scheduleEntity.setDate(scheduleUpdateDto.getDate());
    scheduleEntity.setTime(scheduleUpdateDto.getTime());
  }


  public void deleteSchedule(Long memberId, Long scheduleId) {
    ScheduleEntity scheduleEntity = getScheduleEntity(scheduleId);

    if(MAIN.equals(scheduleEntity.getType())){
      throw new CustomException(CANNOT_DELETE_MAIN_SCHEDULE);
    }

    validateSameUser(memberId, scheduleEntity);

    scheduleRepository.delete(scheduleEntity);
  }

  public List<ScheduleDto> getSchedules(Long studyId, LocalDate date) {
    return scheduleRepository.findWithMemberByStudyIdAndDate(studyId, date).stream()
        .map(entity -> ScheduleDto.fromEntity(entity))
        .collect(Collectors.toList());
  }

  public ScheduleMonthDto getMonthSchedules(Long studyId, String date) {
    YearMonth yearMonth = YearMonth.parse(date);

    LocalDate startDate = yearMonth.atDay(1);
    LocalDate endDate = yearMonth.atEndOfMonth();

    Map<LocalDate, List<ScheduleDto>> collect = scheduleRepository.findWithMemberByStudyIdAndDateBetween(
            studyId, startDate, endDate)
        .stream()
        .map(scheduleEntity -> ScheduleDto.fromEntity(scheduleEntity))
        .collect(Collectors.groupingBy(scheduleDto -> scheduleDto.getDate()));

    return ScheduleMonthDto.from(collect);
  }

  private void validateCreateVote(Long memberId, Long scheduleId, ScheduleUpdateDto scheduleUpdateDto,
      ScheduleEntity scheduleEntity) {
    if (MAIN.equals(scheduleEntity.getType()) && (
        !scheduleEntity.getDate().equals(scheduleUpdateDto.getDate()) || !scheduleEntity.getTime()
            .equals(scheduleUpdateDto.getTime()))) {
      voteService.createChangeVote(memberId, scheduleEntity.getStudy().getStudyId(), scheduleId,
          scheduleUpdateDto);
    }
  }

  private void validateSameUser(Long memberId, ScheduleEntity scheduleEntity) {
    StudyMemberEntity studyMemberEntity = getStudyMemberEntity(memberId,
        scheduleEntity.getStudy().getStudyId());

    if(!memberId.equals(studyMemberEntity.getId())) {
      throw new CustomException(NOT_AUTHOR);
    }
  }

  private StudyMemberEntity getStudyMemberEntity(Long studyId, Long memberId) {
    return joinRepository.findByMemberIdAndStudyIdInStatus(memberId,
            studyId, List.of(LEADER, NORMAL))
        .orElseThrow(() -> new CustomException(NOT_A_STUDY_MEMBER));
  }

  private ScheduleEntity getScheduleEntity(Long scheduleId) {
    return scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));
  }

}
