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
import com.campfiredev.growtogether.notification.service.NotificationService;
import com.campfiredev.growtogether.notification.type.NotiType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BootCampCommentService {

    private final BootCampCommentRepository bootCampCommentRepository;
    private final BootCampReviewRepository bootCampReviewRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    @Transactional
    public void addComment(CommentRequest request , Authentication authentication){

        MemberEntity member = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        BootCampReview review = bootCampReviewRepository.findById(request.getBootCampId())
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        BootCampComment parentComment = null;
        int depth = 0;

        if(request.getParentCommentId() != null){
            parentComment = bootCampCommentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

            depth = parentComment.getDepth() + 1;

            if(depth > 2) throw new CustomException(ErrorCode.COMMENT_DEPTH_EXCEED);
        }


        BootCampComment comment = BootCampComment.builder()
                .commentContent(request.getContent())
                .bootCampReview(review)
                .member(member)
                .parentComment(parentComment)
                .isDeleted(false)
                .depth(depth)
                .build();

        bootCampCommentRepository.save(comment);

        String reviewUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/bootcamp/comments/{id}").buildAndExpand(review.getBootCampId()).toUriString();
        // 추후에 수정 예정
        if(parentComment == null){
            notificationService.sendNotification(review.getMember(),"[부트캠프]"+review.getTitle() +" 새로운 답글이 달렸습니다.", reviewUrl , NotiType.BOOTCAMP);
        }

        if(parentComment != null){
            notificationService.sendNotification(parentComment.getMember(),"[부트캠프]"+review.getTitle() + " 댓글에 새로운 답글이 달렸습니다.", reviewUrl , NotiType.BOOTCAMP);
        }
    }

    @Transactional
    public void updateComment(Long commentId , String newContent, Authentication authentication){

        BootCampComment comment = bootCampCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        MemberEntity member = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!comment.getMember().equals(member)){
            throw new CustomException(ErrorCode.NOT_AUTHOR);
        }

        boolean isDelete = bootCampCommentRepository.existsByBootCampCommentIdAndIsDeletedTrue(comment.getBootCampCommentId());

        if(isDelete){
            throw new CustomException(ErrorCode.COMMENT_ACCESS_DENIED);
        }

        comment.setCommentContent(newContent);

        bootCampCommentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, Authentication authentication){

        BootCampComment comment = bootCampCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        MemberEntity member = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!comment.getMember().equals(member)){
            throw new CustomException(ErrorCode.NOT_AUTHOR);
        }

        comment.setCommentContent("작성자에 의해 삭제된 댓글입니다.");
        comment.setIsDeleted(true);
        bootCampCommentRepository.save(comment);
    }


    public Page<CommentResponseDto> getComments(Long reviewId, Pageable pageable){
        BootCampReview review  = bootCampReviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        Page<BootCampComment> topComments = bootCampCommentRepository.findTopLevelCommentsWithChildren(review,pageable);


        return topComments.map(this::recurDto);
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
