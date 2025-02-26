package com.campfiredev.growtogether.study.controller;

import com.campfiredev.growtogether.study.dto.StudyDTO;
import com.campfiredev.growtogether.study.service.StudyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study")
public class StudyController {
    private final StudyService studyService;

    @PostMapping
    public StudyDTO createStudy(@Valid @RequestBody StudyDTO dto) {
        return studyService.createStudy(dto);
    }

    @GetMapping
    public List<StudyDTO> getAllStudies() {
        return studyService.getAllStudies();
    }
}


