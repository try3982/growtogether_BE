package com.campfiredev.growtogether.bootcamp.controller;

import com.campfiredev.growtogether.bootcamp.dto.CommentRequest;
import com.campfiredev.growtogether.bootcamp.dto.CommentResponseDto;
import com.campfiredev.growtogether.bootcamp.dto.CommentUpdateRequest;
import com.campfiredev.growtogether.bootcamp.service.BootCampCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bootcamp/comments")
public class BootCampCommentController {

    private final BootCampCommentService bootCampCommentService;


    @PostMapping
    public ResponseEntity<String> addComment(@RequestBody CommentRequest request , Authentication authentication){

        bootCampCommentService.addComment(request,authentication);


        return ResponseEntity.ok("댓글 작성이 완료되었습니다.");
    }

    @PutMapping("/{bootCampCommentId}")
    public ResponseEntity<String> updateComment(@PathVariable Long bootCampCommentId, @RequestBody CommentUpdateRequest request,Authentication authentication){

        bootCampCommentService.updateComment(bootCampCommentId,request.getContent(), authentication);

        return ResponseEntity.ok("댓글 수정이 완료되었습니다.");
    }

    @DeleteMapping("/{bootCampCommentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long bootCampCommentId,Authentication authentication){

        bootCampCommentService.deleteComment(bootCampCommentId,authentication);

        return ResponseEntity.ok("댓글 삭제가 완료되었습니다.");
    }

    @GetMapping("/{bootCampId}")
    public ResponseEntity<Page<CommentResponseDto>> getComments(@PathVariable Long bootCampId , @RequestParam(defaultValue = "0") int page){
        Pageable pageable = PageRequest.of(page,10);
        Page<CommentResponseDto> comments = bootCampCommentService.getComments(bootCampId,pageable);
        return ResponseEntity.ok(comments);
    }

}
