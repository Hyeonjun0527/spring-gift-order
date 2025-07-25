package gift.member.application.port.in;

import gift.member.application.port.in.dto.*;
import gift.member.domain.model.Member;

import java.util.List;

public interface MemberUseCase {

    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse loginWithKakao(String code);
    List<Member> getAllMembers();
    Member createMember(CreateMemberRequest request);
    void updateMember(Long id, UpdateMemberRequest request);
    void deleteMember(Long id);
    Member getMemberById(Long memberId);
} 