package com.campfiredev.growtogether.bootcamp.service;

import com.campfiredev.growtogether.bootcamp.dto.CommentRequest;
import com.campfiredev.growtogether.bootcamp.dto.CommentResponseDto;
import com.campfiredev.growtogether.bootcamp.entity.BootCampComment;
import com.campfiredev.growtogether.bootcamp.entity.BootCampReview;
import com.campfiredev.growtogether.bootcamp.repository.BootCampCommentRepository;
import com.campfiredev.growtogether.bootcamp.repository.BootCampReviewRepository;
import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorCode;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BootCampCommentService {

    private final BootCampCommentRepository bootCampCommentRepository;
    private final BootCampReviewRepository bootCampReviewRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void addComment(CommentRequest request){

        MemberEntity member = memberRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        BootCampReview review = bootCampReviewRepository.findById(request.getBootCampId())
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        BootCampComment parentComment = null;

        if(request.getParentCommentId() != null){
            parentComment = bootCampCommentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
        }

        BootCampComment comment = BootCampComment.builder()
                .commentContent(request.getContent())
                .bootCampReview(review)
                .member(member)
                .parentComment(parentComment)
                .isDeleted(false)
                .build();

        bootCampCommentRepository.save(comment);
    }

    @Transactional
    public void updateComment(Long commentId , Long userId, String newContent){

        BootCampComment comment = bootCampCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

/*        if(!comment.getMember().getMemberId().equals(userId)){
            throw new CustomException(ErrorCode.COMMENT_ACCESS_DENIED);
        }*/

        comment.setCommentContent(newContent);

        bootCampCommentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId){

        BootCampComment comment = bootCampCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        //본인 이외에 글은 수정 할 수 없는 로직 추가 예정

        comment.setCommentContent("작성자에 의해 삭제된 댓글입니다.");
        comment.setIsDeleted(true);
        bootCampCommentRepository.save(comment);
    }


    public List<CommentResponseDto> getComments(Long reviewId){
        BootCampReview review  = bootCampReviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        List<BootCampComment> topComments = bootCampCommentRepository.findTopLevelCommentsWithChildren(review);

        List<CommentResponseDto> res = new ArrayList<>();

        for(BootCampComment comment : topComments){
            res.add(recurDto(comment));
        }
        return res;
    }

    private CommentResponseDto recurDto(BootCampComment comment) {

        List<CommentResponseDto> childDtos = new ArrayList<>();

        for(BootCampComment child : comment.getChildComments()){
            childDtos.add(recurDto(child));
        }
        return new CommentResponseDto(
                comment.getBootCampCommentId(),
                comment.getIsDeleted() ? "작성자에 의해 삭제된 댓글입니다." : comment.getCommentContent(),
                comment.getMember().getMemberId(),
                comment.getMember().getNickName(),
                comment.getIsDeleted(),
                childDtos
        );
    }



}
