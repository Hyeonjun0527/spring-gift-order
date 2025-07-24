package gift.member.application.usecase;

import gift.common.exception.ForbiddenException;
import gift.common.jwt.JwtTokenPort;
import gift.common.security.PasswordEncoder;
import gift.member.application.port.in.MemberUseCase;
import gift.member.application.port.in.dto.*;
import gift.member.domain.model.Member;
import gift.member.domain.port.out.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MemberInteractor implements MemberUseCase {

    private final MemberRepository memberRepository;
    private final JwtTokenPort jwtTokenPort;
    private final PasswordEncoder passwordEncoder;

    public MemberInteractor(
            MemberRepository memberRepository,
            JwtTokenPort jwtTokenPort,
            PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.jwtTokenPort = jwtTokenPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        Member member = Member.create(request.email(), encodedPassword);
        Member savedMember = memberRepository.save(member);

        return createAuthResponse(savedMember.id(), savedMember.email(), savedMember.role());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new ForbiddenException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.password(), member.password())) {
            throw new ForbiddenException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        return createAuthResponse(member.id(), member.email(), member.role());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Override
    public Member createMember(CreateMemberRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        Member member = Member.create(request.email(), encodedPassword);
        return memberRepository.save(member);
    }

    @Override
    public void updateMember(Long id, UpdateMemberRequest request) {
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        String encodedPassword = request.password() != null ?
                passwordEncoder.encode(request.password()) : existingMember.password();

        Member updatedMember = Member.of(
                existingMember.id(),
                request.email() != null ? request.email() : existingMember.email(),
                encodedPassword,
                request.role() != null ? request.role() : existingMember.role(),
                existingMember.createdAt()
        );

        memberRepository.save(updatedMember);
    }

    @Override
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new IllegalArgumentException("회원을 찾을 수 없습니다.");
        }
        memberRepository.deleteById(id);
    }

    @Override
    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. id: " + memberId));
    }

    private AuthResponse createAuthResponse(Long memberId, String email, gift.member.domain.model.Role role) {
        String accessToken = jwtTokenPort.createAccessToken(memberId, email, role);
        return new AuthResponse(accessToken);
    }
}