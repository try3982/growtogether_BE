package com.campfiredev.growtogether.member.dto;

import com.campfiredev.growtogether.member.entity.MemberSkillEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyPageUpdateDto {

    private String nickName;
    private String phone;
    private String githubUrl;
    private String profileImgUrl;
    private List<MemberSkillEntity> skills;

}
