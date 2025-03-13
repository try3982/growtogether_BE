package com.campfiredev.growtogether.study.controller;

import com.campfiredev.growtogether.study.dto.post.PagedStudyDTO;
import com.campfiredev.growtogether.study.dto.post.StudyDTO;
import com.campfiredev.growtogether.study.service.StudyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study")
public class StudyController {
    private final StudyService studyService;

    @PostMapping
    public StudyDTO createStudy(@Valid @RequestBody StudyDTO dto) {
        long userId = 1;
        return studyService.createStudy(dto, userId);
    }

    @GetMapping
    public PagedStudyDTO getAllStudies(@RequestParam(defaultValue = "0") int page){
        Pageable pageable = PageRequest.of(page-1, 9);
        return studyService.getAllStudies(pageable);
    }

    @GetMapping("/{studyId}")
    public StudyDTO getStudyById(@PathVariable Long studyId) {
        return studyService.getStudyById(studyId);
    }

    @PutMapping("/{studyId}")
    public StudyDTO updateStudy(@PathVariable Long studyId, @Valid @RequestBody StudyDTO dto) {
        return studyService.updateStudy(studyId, dto);
    }

    @DeleteMapping("/{studyId}")
    public void deleteStudy(@PathVariable Long studyId) {
        studyService.deleteStudy(studyId);
    }
}


