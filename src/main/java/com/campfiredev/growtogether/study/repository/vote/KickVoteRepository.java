package com.campfiredev.growtogether.study.repository.vote;

import com.campfiredev.growtogether.study.entity.vote.KickVoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KickVoteRepository extends JpaRepository<KickVoteEntity, Long> {

}
