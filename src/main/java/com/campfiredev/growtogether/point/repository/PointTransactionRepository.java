package com.campfiredev.growtogether.point.repository;

import com.campfiredev.growtogether.point.entity.PointTransaction;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
    List<PointTransaction> findByMemberOrderByDateDesc(MemberEntity member);
}
