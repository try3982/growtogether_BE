package com.campfiredev.growtogether.bootcamp.repository;

import com.campfiredev.growtogether.bootcamp.entity.BootCampReview;
import com.campfiredev.growtogether.bootcamp.entity.BootCampSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BootCampSkillRepository extends JpaRepository<BootCampSkill, Long> {
    void deleteByBootCampReview(BootCampReview review);
}
