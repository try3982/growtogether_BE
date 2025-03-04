package com.campfiredev.growtogether.bootcamp.controller;

import com.campfiredev.growtogether.bootcamp.dto.CommentRequest;
import com.campfiredev.growtogether.bootcamp.dto.CommentResponseDto;
import com.campfiredev.growtogether.bootcamp.dto.CommentUpdateRequest;
import com.campfiredev.growtogether.bootcamp.service.BootCampCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bootcamp/comments")
public class BootCampCommentController {

    private final BootCampCommentService bootCampCommentService;


    @PostMapping
    public ResponseEntity<String> addComment(@RequestBody CommentRequest request){

        bootCampCommentService.addComment(request);


        return ResponseEntity.ok("댓글 작성이 완료되었습니다.");
    }

    @PutMapping("/{bootCampCommentId}")
    public ResponseEntity<String> updateComment(@PathVariable Long bootCampCommentId, @RequestBody CommentUpdateRequest request){

        bootCampCommentService.updateComment(bootCampCommentId,request.getUserId(),request.getContent());

        return ResponseEntity.ok("댓글 수정이 완료되었습니다.");
    }

    @DeleteMapping("/{bootCampCommentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long bootCampCommentId){

        bootCampCommentService.deleteComment(bootCampCommentId);

        return ResponseEntity.ok("댓글 삭제가 완료되었습니다.");
    }

    @GetMapping("/{bootCampCommentId}")
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable Long bootCampCommentId){
        List<CommentResponseDto> comments = bootCampCommentService.getComments(bootCampCommentId);
        return ResponseEntity.ok(comments);
    }

}
