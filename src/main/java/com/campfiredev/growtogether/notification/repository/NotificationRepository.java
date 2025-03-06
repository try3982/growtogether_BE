package com.campfiredev.growtogether.notification.repository;

import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification , Long> {

    // 특정 사용자 읽지 않는 알림 조회
    List<Notification> findByMemberAndIsCheckFalseOrderByCreatedAtDesc(MemberEntity member);


}
