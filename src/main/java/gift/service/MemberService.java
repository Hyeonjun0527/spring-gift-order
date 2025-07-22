package gift.service;

import gift.dto.LoginRequestDto;
import gift.dto.MemberRequestDto;
import gift.dto.MemberResponseDto;
import gift.dto.TokenResponseDto;
import gift.entity.Member;
import gift.entity.MemberRole;
import gift.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    public MemberService(MemberRepository memberRepository, JwtService jwtService) {
        this.memberRepository = memberRepository;
        this.jwtService = jwtService;
    }

    // 회원 가입
    @Transactional
    public TokenResponseDto register(MemberRequestDto requestDto) {
        // 중복 이메일 검사
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 회원 생성
        Member member = new Member(requestDto.getEmail(), requestDto.getPassword(), MemberRole.USER);
        Member savedMember = memberRepository.save(member);

        // JWT 토큰 생성 (역할 정보 포함)
        String token = jwtService.generateToken(savedMember.getId(), savedMember.getRole().name());
        
        return new TokenResponseDto(token);
    }

    // 로그인
    public TokenResponseDto login(LoginRequestDto requestDto) {
        // 이메일로 회원 조회
        Member member = memberRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        // 비밀번호 검증
        if (!member.getPassword().equals(requestDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 생성 (역할 정보 포함)
        String token = jwtService.generateToken(member.getId(), member.getRole().name());
        
        return new TokenResponseDto(token);
    }

    // 회원 조회 (ID로)
    @Transactional(readOnly = true)
    public MemberResponseDto getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        
        return new MemberResponseDto(member);
    }

    // 회원 조회 (이메일로)
    @Transactional(readOnly = true)
    public MemberResponseDto getMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        
        return new MemberResponseDto(member);
    }

    // 전체 회원 조회
    @Transactional(readOnly = true)
    public List<MemberResponseDto> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(MemberResponseDto::new)
                .collect(Collectors.toList());
    }

    // 회원 삭제
    @Transactional
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new NoSuchElementException("존재하지 않는 회원입니다.");
        }
        memberRepository.deleteById(id);
    }

    // 회원 수정
    @Transactional
    public MemberResponseDto updateMember(Long id, MemberRequestDto dto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        member.setEmail(dto.getEmail());
        member.setPassword(dto.getPassword());
        return new MemberResponseDto(member); // JPA가 자동으로 변경된 엔티티를 저장함
    }
} 