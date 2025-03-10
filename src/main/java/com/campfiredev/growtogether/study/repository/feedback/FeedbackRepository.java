package com.campfiredev.growtogether.study.repository.feedback;

import com.campfiredev.growtogether.study.entity.feedback.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long> {

}
