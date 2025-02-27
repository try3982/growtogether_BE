package com.campfiredev.growtogether.skill.repository;

import com.campfiredev.growtogether.skill.entity.SkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<SkillEntity, Long> {
    List<SkillEntity> findBySkillNameIn(List<String> skillNames);
}