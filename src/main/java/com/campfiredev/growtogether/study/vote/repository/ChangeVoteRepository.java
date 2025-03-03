package com.campfiredev.growtogether.study.vote.repository;

import com.campfiredev.growtogether.study.vote.entity.ChangeVoteEntity;
import com.campfiredev.growtogether.study.vote.entity.KickVoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeVoteRepository extends JpaRepository<ChangeVoteEntity, Long> {

}
