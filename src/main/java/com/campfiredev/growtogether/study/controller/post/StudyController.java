package com.campfiredev.growtogether.study.controller.post;

import com.campfiredev.growtogether.member.dto.CustomUserDetails;
import com.campfiredev.growtogether.study.dto.post.PagedStudyDTO;
import com.campfiredev.growtogether.study.dto.post.StudyDTO;
import com.campfiredev.growtogether.study.service.post.StudyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study")
public class StudyController {
    private final StudyService studyService;

    @PostMapping
    public StudyDTO createStudy(@Valid @RequestBody StudyDTO dto, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return studyService.createStudy(dto, customUserDetails.getMemberId());
    }

    @GetMapping
    public PagedStudyDTO getAllStudies(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "9") int size
    ){
        Pageable pageable = PageRequest.of(page-1, size);
        return studyService.getAllStudies(pageable);
    }

    @GetMapping("/{studyId}")
    public StudyDTO getStudyById(@PathVariable Long studyId) {
        return studyService.getStudyById(studyId);
    }

    @PutMapping("/{studyId}")
    public StudyDTO updateStudy(@PathVariable Long studyId, @Valid @RequestBody StudyDTO dto,@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return studyService.updateStudy(studyId, dto,customUserDetails.getMemberId());
    }

    @DeleteMapping("/{studyId}")
    public void deleteStudy(@PathVariable Long studyId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        studyService.deleteStudy(studyId,customUserDetails.getMemberId());
    }

    @GetMapping("/popular")
    public List<StudyDTO> getPopularStudies() {
        return studyService.getPopularStudies();
    }
}


