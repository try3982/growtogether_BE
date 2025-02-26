package com.campfiredev.growtogether.study.service;

import com.campfiredev.growtogether.study.dto.StudyDTO;
import com.campfiredev.growtogether.study.entity.Study;
import com.campfiredev.growtogether.study.entity.StudyStatus;
import com.campfiredev.growtogether.study.repository.StudyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @Mock
    private StudyRepository studyRepository;

    @InjectMocks
    private StudyService studyService;

    private Study study;
    private StudyDTO studyDTO;

    @BeforeEach
    void setUp() {
        study = Study.builder()
                .studyId(1L)
                .title("Study Title")
                .description("Study Description")
                .viewCount(0L)
                .maxParticipant(10)
                .studyStartDate(new Date())
                .studyEndDate(new Date())
                .isActive(true)
                .studyStatus(StudyStatus.PROGESS)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .participant(0)
                .type("Online")
                .studyCount("1/10")
                .build();

        studyDTO = StudyDTO.builder()
                .studyId(study.getStudyId())
                .title(study.getTitle())
                .description(study.getDescription())
                .viewCount(study.getViewCount())
                .maxParticipant(study.getMaxParticipant())
                .studyStartDate(study.getStudyStartDate())
                .studyEndDate(study.getStudyEndDate())
                .isActive(study.getIsActive())
                .studyStatus(study.getStudyStatus())
                .createdAt(study.getCreatedAt())
                .updatedAt(study.getUpdatedAt())
                .participant(study.getParticipant())
                .type(study.getType())
                .studyCount(study.getStudyCount())
                .build();
    }

    @Test
    void createStudy_shouldReturnSavedStudyDTO() {
        when(studyRepository.save(any(Study.class))).thenReturn(study);

        StudyDTO createdStudy = studyService.createStudy(studyDTO);

        assertNotNull(createdStudy);
        assertEquals(studyDTO.getTitle(), createdStudy.getTitle());
        verify(studyRepository, times(1)).save(any(Study.class));
    }

    @Test
    void getAllStudies_shouldReturnListOfStudyDTOs() {
        when(studyRepository.findAll()).thenReturn(Collections.singletonList(study));

        List<StudyDTO> studies = studyService.getAllStudies();

        assertNotNull(studies);
        assertEquals(1, studies.size());
        verify(studyRepository, times(1)).findAll();
    }
}
