package com.campfiredev.growtogether.study.service;

import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorCode;
import com.campfiredev.growtogether.skill.entity.SkillEntity;
import com.campfiredev.growtogether.skill.repository.SkillRepository;
import com.campfiredev.growtogether.study.dto.StudyDTO;
import com.campfiredev.growtogether.study.entity.SkillStudy;
import com.campfiredev.growtogether.study.entity.Study;
import com.campfiredev.growtogether.study.repository.SkillStudyRepository;
import com.campfiredev.growtogether.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.campfiredev.growtogether.exception.response.ErrorCode.END_DATE_AFTER_START_DATE;
import static com.campfiredev.growtogether.exception.response.ErrorCode.START_DATE_PAST;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final SkillRepository skillRepository;
    private final SkillStudyRepository skillStudyRepository;

    public StudyDTO createStudy(StudyDTO dto) {
        validateDates(dto.getStudyStartDate(), dto.getStudyEndDate());
        
        List<SkillEntity> skills = skillRepository.findBySkillNameIn(dto.getSkillNames());

        if(dto.getSkillNames().size() != skills.size()){
            throw new CustomException(ErrorCode.INVALID_SKILL);
        }

        Study study = Study.fromDTO(dto);

        List<SkillStudy> skillStudies = skills.stream()
                .map(skill -> SkillStudy.builder()
                        .skill(skill)
                        .study(studyRepository.save(study))
                        .build())
                .toList();

        study.addSkillStudies(skillStudyRepository.saveAll(skillStudies));

        return StudyDTO.fromEntity(study);
    }

    private void validateDates(Date studyStartDate, Date studyEndDate) {
        Date currentDate = new Date();

        if (studyStartDate.before(currentDate)) {
            throw new CustomException(START_DATE_PAST);
        }

        if (studyEndDate.before(studyStartDate)) {
            throw new CustomException(END_DATE_AFTER_START_DATE);
        }
    }

    public List<StudyDTO> getAllStudies() {
        return studyRepository.findAll().stream()
                .map(StudyDTO::fromEntity)
                .toList();
    }
}
