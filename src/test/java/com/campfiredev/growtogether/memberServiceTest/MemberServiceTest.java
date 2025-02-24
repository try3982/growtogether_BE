package com.campfiredev.growtogether.memberServiceTest;


import com.campfiredev.growtogether.member.dto.MemberDto;
import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.repository.MemberRepository;
import com.campfiredev.growtogether.member.repository.UserSkillRepository;
import com.campfiredev.growtogether.member.service.MemberService;
import com.campfiredev.growtogether.skill.repository.SkillRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserSkillRepository userSkillRepository;

    @Mock
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    @DisplayName("회원가입 성공 테스트")
    void register_success() {
        // Given (입력값 설정)
        MemberDto request = new MemberDto();
        request.setNickName("testUser");
        request.setEmail("test@example.com");
        request.setPhone("01012345678");
        request.setPassword("Password123!");

        // 중복 검사 - 중복 없음 (모두 false 반환)
        when(memberRepository.existsByEmail(anyString())).thenReturn(false);
        when(memberRepository.existsByNickName(anyString())).thenReturn(false);
        when(memberRepository.existsByPhone(anyString())).thenReturn(false);

        // 비밀번호 암호화
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");

        // 회원 저장 Mock 설정
        MemberEntity savedMember = MemberEntity.builder()
                .userId(1L)
                .nickName(request.getNickName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password("$2a$10$hashedPassword")  // 암호화된 비밀번호
                .build();

        when(memberRepository.save(any(MemberEntity.class))).thenReturn(savedMember);

        // When (회원가입 실행)
        MemberEntity result = memberService.register(request);

        // Then (검증)
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(request.getNickName(), result.getNickName());
        assertEquals(request.getEmail(), result.getEmail());
        assertEquals(request.getPhone(), result.getPhone());

        // 비밀번호 암호화 검증 (평문 비밀번호와 다름)
        assertNotEquals(request.getPassword(), result.getPassword());
        assertTrue(result.getPassword().startsWith("$2a$")); // BCrypt 해시인지 확인

        // Mock 메서드 호출 여부 확인
        verify(memberRepository, times(1)).existsByEmail(request.getEmail());
        verify(memberRepository, times(1)).existsByNickName(request.getNickName());
        verify(memberRepository, times(1)).existsByPhone(request.getPhone());
        verify(passwordEncoder, times(1)).encode(request.getPassword());
        verify(memberRepository, times(1)).save(any(MemberEntity.class));
    }

    @Test
    @DisplayName("중복된 이메일로 회원가입 실패")
    void register_fail_duplicate_email() {
        // Given
        MemberDto request = new MemberDto();
        request.setEmail("test@example.com");

        when(memberRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then (예외 발생 검증)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            memberService.register(request);
        });

        assertEquals("이미 사용 중인 이메일입니다.", exception.getMessage());
        verify(memberRepository, times(1)).existsByEmail(request.getEmail());
    }

    @Test
    @DisplayName("중복된 닉네임으로 회원가입 실패")
    void register_fail_duplicate_nickname() {
        // Given
        MemberDto request = new MemberDto();
        request.setNickName("testUser");

        when(memberRepository.existsByNickName(anyString())).thenReturn(true);

        // When & Then (예외 발생 검증)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            memberService.register(request);
        });

        assertEquals("이미 사용 중인 닉네임입니다.", exception.getMessage());
        verify(memberRepository, times(1)).existsByNickName(request.getNickName());
    }

    @Test
    @DisplayName("중복된 전화번호로 회원가입 실패")
    void register_fail_duplicate_phone() {
        // Given
        MemberDto request = new MemberDto();
        request.setPhone("01012345678");

        when(memberRepository.existsByPhone(anyString())).thenReturn(true);

        // When & Then (예외 발생 검증)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            memberService.register(request);
        });

        assertEquals("이미 사용 중인 전화번호입니다.", exception.getMessage());
        verify(memberRepository, times(1)).existsByPhone(request.getPhone());
    }
}
