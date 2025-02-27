package com.campfiredev.growtogether.member.repository;

import com.campfiredev.growtogether.member.entity.UserSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkillEntity, Long> {

}