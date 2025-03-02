package com.campfiredev.growtogether.bootcamp.repository;

import com.campfiredev.growtogether.bootcamp.entity.BootCampReview;
import com.campfiredev.growtogether.bootcamp.entity.ReviewLike;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike , Long> {

    boolean existsByBootCampReviewAndMember(BootCampReview bootCampReview, MemberEntity member);

    void deleteByBootCampReviewAndMember(BootCampReview bootCampReview, MemberEntity member);

}
