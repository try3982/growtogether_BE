package com.campfiredev.growtogether.study.service.join;

import com.campfiredev.growtogether.study.dto.join.StudyMemberListDto;

public interface JoinService {

  void join(Long memberId, Long studyId);

  void confirmJoin(Long studyMemberId);

  void cancelJoin(Long studyMemberId);

  StudyMemberListDto getPendingList(Long studyId);

  StudyMemberListDto getJoinList(Long studyId);
}
