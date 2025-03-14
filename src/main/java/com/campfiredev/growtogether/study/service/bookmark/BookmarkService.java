package com.campfiredev.growtogether.study.service.bookmark;

import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorCode;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.repository.MemberRepository;
import com.campfiredev.growtogether.study.entity.Bookmark;
import com.campfiredev.growtogether.study.entity.Study;
import com.campfiredev.growtogether.study.repository.BookmarkRepository;
import com.campfiredev.growtogether.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;

    public void setBookMark(Long memberId, Long studyId) {
        Optional<Bookmark> checkBookmark= bookmarkRepository.findByMember_MemberIdAndStudy_StudyId(memberId, studyId);
        if(checkBookmark.isPresent()){
            bookmarkRepository.delete(checkBookmark.get());
        }else{
            Study study = studyRepository.findById(studyId)
                    .orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));

            MemberEntity member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_INVALID_MEMBER));

            Bookmark bookmark = Bookmark.builder()
                    .member(member)
                    .study(study)
                    .build();

            bookmarkRepository.save(bookmark);
        }
    }
}

