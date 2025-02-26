package com.campfiredev.growtogether.member.service;

import com.campfiredev.growtogether.mail.service.EmailService;
import com.campfiredev.growtogether.member.dto.MemberDto;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.entity.UserSkillEntity;
import com.campfiredev.growtogether.member.repository.MemberRepository;
import com.campfiredev.growtogether.member.repository.UserSkillRepository;
import com.campfiredev.growtogether.skill.repository.SkillRepository;
import com.campfiredev.growtogether.skill.entity.SkillEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final S3Service s3Service;

    public MemberEntity register(MemberDto request, MultipartFile profileImage) {
        // 이메일 인증 여부 확인
        if (!emailService.verifyCode(request.getEmail(), request.getVerificationCode())) {
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
        }

        // 중복 검사
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (memberRepository.existsByNickName(request.getNickName())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        if (memberRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.");
        }

        // 프로필 이미지 업로드 후 파일 키 저장
        String profileImageKey = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageKey = s3Service.uploadFile(profileImage);
        }

        // 회원 저장
        MemberEntity member = memberRepository.save(MemberEntity.builder()
                .nickName(request.getNickName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .githubUrl(request.getGithubUrl())
                .profileImageKey(profileImageKey)
                .build());

        // 선택한 기술 스택 저장
        if (request.getSkills() != null && !request.getSkills().isEmpty()) {
            List<SkillEntity> skills = skillRepository.findAllById(request.getSkills());
            for (SkillEntity skill : skills) {
                userSkillRepository.save(new UserSkillEntity(member, skill));
            }
        }

        return member;
    }
}
