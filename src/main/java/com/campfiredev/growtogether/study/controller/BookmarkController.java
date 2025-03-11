package com.campfiredev.growtogether.study.controller;

import com.campfiredev.growtogether.study.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study/bookmark")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/{studyId}")
    public void addBookmark(@PathVariable long studyId) {
        bookmarkService.setBookMark(1L,studyId);
    }
}

