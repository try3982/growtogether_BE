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
import com.campfiredev.growtogether.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.campfiredev.growtogether.exception.response.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyService {

    private final StudyRepository studyRepository;

    private final SkillRepository skillRepository;

    private final SkillStudyRepository skillStudyRepository;

    private final MemberRepository memberRepository;

    @Transactional
    public StudyDTO createStudy(StudyDTO dto, long userId) {
        validateDates(dto.getStudyStartDate(), dto.getStudyEndDate());

        List<SkillEntity> skills = skillRepository.findBySkillNameIn(dto.getSkillNames());

        if (dto.getSkillNames().size() != skills.size()) {
            throw new CustomException(ErrorCode.INVALID_SKILL);
        }

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

    private void validateDates(Date studyStartDate, Date studyEndDate) {
        Date currentDate = new Date();

        if (studyStartDate.before(currentDate)) {
            throw new CustomException(START_DATE_PAST);
        }

        if (studyEndDate.before(studyStartDate)) {
            throw new CustomException(END_DATE_AFTER_START_DATE);
        }
    }

    public List<StudyDTO> getAllStudies() {
        return studyRepository.findByIsDeletedFalseOrderByCreatedAtDesc().stream()
                .map(StudyDTO::fromEntity)
                .toList();
    }

    public StudyDTO getStudyById(Long studyId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(STUDY_NOT_FOUND));

        if(study.getIsDeleted()){
            throw new CustomException(ALREADY_DELETED_STUDY);
        }
        return StudyDTO.fromEntity(study);
    }
}
