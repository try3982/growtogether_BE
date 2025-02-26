package com.campfiredev.growtogether.study.service;

import com.campfiredev.growtogether.study.dto.StudyDTO;
import com.campfiredev.growtogether.study.entity.Skill;
import com.campfiredev.growtogether.study.entity.SkillStudy;
import com.campfiredev.growtogether.study.entity.Study;
import com.campfiredev.growtogether.study.repository.SkillRepository;
import com.campfiredev.growtogether.study.repository.SkillStudyRepository;
import com.campfiredev.growtogether.study.repository.StudyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillStudyRepository skillStudyRepository;

    @InjectMocks
    private StudyService studyService;

    @Test
    void createStudy() {
        // Given
        List<String> skillNames = Arrays.asList("Java", "Spring");
        StudyDTO dto = StudyDTO.builder()
                .title("New Study")
                .description("Study Description")
                .skillNames(skillNames)
                .build();

        Skill javaSkill = Skill.builder().skillName("Java").build();
        Skill springSkill = Skill.builder().skillName("Spring").build();
        List<Skill> skills = Arrays.asList(javaSkill, springSkill);

        Study savedStudy = Study.fromDTO(dto);
        List<SkillStudy> skillStudies = skills.stream()
                .map(skill -> SkillStudy.builder()
                        .skill(skill)
                        .study(savedStudy)
                        .build())
                .toList();

        // When
        when(skillRepository.findBySkillNameIn(skillNames)).thenReturn(skills);
        when(studyRepository.save(any(Study.class))).thenReturn(savedStudy);
        when(skillStudyRepository.saveAll(anyList())).thenReturn(skillStudies);

        StudyDTO result = studyService.createStudy(dto);

        // Then
        assertEquals(dto.getTitle(), result.getTitle());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getSkillNames().size(), result.getSkillNames().size());

        verify(skillRepository, times(1)).findBySkillNameIn(skillNames);
        verify(studyRepository, times(2)).save(any(Study.class));
        verify(skillStudyRepository, times(1)).saveAll(anyList());
    }

    @Test
    void getAllStudies() {
        // Given
        Study study1 = Study.builder().title("Study 1").description("Description 1").build();
        Study study2 = Study.builder().title("Study 2").description("Description 2").build();
        study1.addSkillStudies(new ArrayList<>());
        study2.addSkillStudies(new ArrayList<>());
        List<Study> studies = Arrays.asList(study1, study2);

        // When
        when(studyRepository.findAll()).thenReturn(studies);

        List<StudyDTO> result = studyService.getAllStudies();

        // Then
        assertEquals(2, result.size());
        assertEquals("Study 1", result.get(0).getTitle());
        assertEquals("Study 2", result.get(1).getTitle());

        verify(studyRepository, times(1)).findAll();
    }
}
