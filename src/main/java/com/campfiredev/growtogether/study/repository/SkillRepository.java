package com.campfiredev.growtogether.study.repository;

import com.campfiredev.growtogether.study.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findBySkillNameIn(List<String> skillNames);
}



