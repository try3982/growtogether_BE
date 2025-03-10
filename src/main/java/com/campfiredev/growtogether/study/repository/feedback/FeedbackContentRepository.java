package com.campfiredev.growtogether.study.repository.feedback;

import com.campfiredev.growtogether.study.entity.feedback.FeedbackContentEntity;
import com.campfiredev.growtogether.study.entity.join.StudyMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackContentRepository extends JpaRepository<FeedbackContentEntity, Long> {

  Long countByStudyMember(StudyMemberEntity studyMemberEntity);

}
