package com.campfiredev.growtogether.study.service;

import com.campfiredev.growtogether.study.dto.StudyDTO;
import com.campfiredev.growtogether.study.entity.Skill;
import com.campfiredev.growtogether.study.entity.SkillStudy;
import com.campfiredev.growtogether.study.entity.Study;
import com.campfiredev.growtogether.study.repository.SkillRepository;
import com.campfiredev.growtogether.study.repository.SkillStudyRepository;
import com.campfiredev.growtogether.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final SkillRepository skillRepository;
    private final SkillStudyRepository skillStudyRepository;

    public StudyDTO createStudy(StudyDTO dto) {
        List<Skill> skills = skillRepository.findBySkillNameIn(dto.getSkillNames());
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

    public List<StudyDTO> getAllStudies() {
        return studyRepository.findAll().stream()
                .map(StudyDTO::fromEntity)
                .toList();
    }
}
