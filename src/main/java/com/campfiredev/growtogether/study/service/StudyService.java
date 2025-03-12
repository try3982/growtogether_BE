package com.campfiredev.growtogether.study.service;

import com.campfiredev.growtogether.exception.custom.CustomException;
import com.campfiredev.growtogether.exception.response.ErrorCode;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.repository.MemberRepository;
import com.campfiredev.growtogether.skill.entity.SkillEntity;
import com.campfiredev.growtogether.skill.repository.SkillRepository;
import com.campfiredev.growtogether.study.dto.StudyDTO;
import com.campfiredev.growtogether.study.entity.SkillStudy;
import com.campfiredev.growtogether.study.entity.Study;
import com.campfiredev.growtogether.study.repository.SkillStudyRepository;
import com.campfiredev.growtogether.study.repository.StudyCommentRepository;
import com.campfiredev.growtogether.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.campfiredev.growtogether.exception.response.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyService {

    private final StudyRepository studyRepository;

    private final SkillRepository skillRepository;

    private final SkillStudyRepository skillStudyRepository;

    private final MemberRepository memberRepository;

    private final StudyCommentRepository studyCommentRepository;

    public StudyDTO createStudy(StudyDTO dto, long userId) {
        List<SkillEntity> skills = validate(dto);

        Study study = Study.fromDTO(dto);

//        해당부분은 아직 회원가입 api가 미완성 단계여서 주석처리 추후에 로직 변경예정
//        MemberEntity member = memberRepository.findById(1L).orElseThrow(()->new CustomException(NOT_INVALID_MEMBER));
        MemberEntity member = memberRepository.save(MemberEntity.builder()
                .nickName(Math.random() + "")
                .email(Math.random() + "")
                .phone(Math.random() + "")
                .password(Math.random() + "")
                .build()
        );

        study.setAuthor(member);

        Study savedStudy = studyRepository.save(study);

        List<SkillStudy> skillStudies = skills.stream()
                .map(skill -> SkillStudy.builder()
                        .skill(skill)
                        .study(savedStudy)
                        .build())
                .toList();

        study.addSkillStudies(skillStudyRepository.saveAll(skillStudies));

        return StudyDTO.fromEntity(study);
    }
    @Transactional(readOnly = true)
    public List<StudyDTO> getAllStudies() {
        return studyRepository.findByIsDeletedFalseOrderByCreatedAtDesc().stream()
                .map(StudyDTO::fromEntity)
                .peek(studyDTO -> studyDTO.setCommentCount(studyCommentRepository.countAllByStudyId(studyDTO.getStudyId())))
                .toList();
    }

    public StudyDTO getStudyById(Long studyId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(STUDY_NOT_FOUND));

        if(Boolean.TRUE.equals(study.getIsDeleted())){
            throw new CustomException(ALREADY_DELETED_STUDY);
        }

        study.updateViewCount();
        return StudyDTO.fromEntity(study);
    }

    public StudyDTO updateStudy(Long studyId, StudyDTO dto) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(STUDY_NOT_FOUND));

        List<SkillEntity> newSkills = validate(dto);
        List<SkillStudy> existSkillStudies = study.getSkillStudies();

        List<SkillStudy> toRemove = existSkillStudies.stream()
                .filter(skillStudy -> !newSkills.contains(skillStudy.getSkill()))
                .toList();

        existSkillStudies.removeAll(toRemove);
        skillStudyRepository.deleteAll(toRemove);

        List<SkillStudy> newSkillStudies = newSkills.stream()
                .filter(skill -> existSkillStudies.stream().noneMatch(skillStudy -> skillStudy.getSkill().equals(skill)))
                .map(skill -> SkillStudy.builder()
                        .skill(skill)
                        .study(study)
                        .build())
                .toList();

        skillStudyRepository.saveAll(newSkillStudies);

        study.updateFromDto(dto,newSkillStudies);

        return StudyDTO.fromEntity(study);
    }

    private List<SkillEntity> validate(StudyDTO dto) {
        List<SkillEntity> skills = skillRepository.findBySkillNameIn(dto.getSkillNames());

        if (dto.getSkillNames().size() != skills.size()) {
            throw new CustomException(INVALID_SKILL);
        }

        return skills;
    }

    public void deleteStudy(Long studyId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));
        study.setIsDeleted(true);
        studyRepository.save(study);
    }
}
