package gift.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gift.config.JwtUtil;
import gift.dto.MemberRequest;
import gift.dto.MemberResponse;
import gift.dto.PaginationResponse;
import gift.dto.TokenResponse;
import gift.entity.Member;
import gift.exception.DuplicateEmailException;
import gift.exception.InvalidPasswordException;
import gift.exception.MemberNotFoundException;
import gift.repository.MemberRepository;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerNormalCaseTokenResponse() {
        MemberRequest request = new MemberRequest(
            "test@test.com",
            "password123",
            "USER"
        );
        Member savedMember = new Member(
            1L,
            "test@test.com",
            "password123",
            "USER"
        );
        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);
        when(jwtUtil.generateToken(savedMember)).thenReturn("mocked-token");

        TokenResponse response = memberService.register(request);

        assertThat(response.token()).isEqualTo("mocked-token");
        verify(memberRepository).save(any(Member.class));
        verify(jwtUtil).generateToken(savedMember);
    }

    @Test
    void registerDuplicateCaseException() {
        MemberRequest request = new MemberRequest(
            "test@test.com",
            "password123",
            "USER"
        );
        Member existingMember = new Member(
            1L,
            "test@test.com",
            "password123",
            "USER"
        );
        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(existingMember));

        assertThatThrownBy(() -> memberService.register(request))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessage("이미 존재하는 이메일입니다.");
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void loginNormalCaseTokenResponse() {
        MemberRequest request = new MemberRequest(
            "test@test.com",
            "password123",
            "USER"
        );
        String encodedPassword = Base64.getEncoder().encodeToString("password123".getBytes());
        Member member = new Member(
            1L,
            "test@test.com",
            encodedPassword,
            "USER"
        );
        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(member));
        when(jwtUtil.generateToken(member)).thenReturn("mocked-token");

        TokenResponse response = memberService.login(request);

        assertThat(response.token()).isEqualTo("mocked-token");
        verify(jwtUtil).generateToken(member);
    }

    @Test
    void loginWrongEmailException() {
        MemberRequest request = new MemberRequest(
            "notexisting@test.com",
            "password123",
            "USER"
        );
        when(memberRepository.findByEmail("notexisting@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.login(request))
            .isInstanceOf(MemberNotFoundException.class)
            .hasMessage("이메일이 올바르지 않습니다.");
    }

    @Test
    void loginWrongPasswordException() {
        MemberRequest request = new MemberRequest(
            "test@test.com",
            "wrong123",
            "USER"
        );
        Member member = new Member(
            1L,
            "test@test.com",
            "password123",
            "USER"
        );
        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> memberService.login(request))
            .isInstanceOf(InvalidPasswordException.class)
            .hasMessage("비밀번호가 올바르지 않습니다.");
    }

    @Test
    void updateMemberNormalCaseTokenResponse() {
        MemberRequest request = new MemberRequest(
            "test@test.com",
            "newpassword123",
            "USER"
        );
        Member existingMember = new Member(
            1L,
            "test@test.com",
            Base64.getEncoder().encodeToString("password123".getBytes()),
            "USER"
        );
        existingMember.updatePassword("newpassword123");
        Member updatedMember = new Member(
            1L,
            "test@test.com",
            Base64.getEncoder().encodeToString("newpassword123".getBytes()),
            "USER"
        );
        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(existingMember));
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
            Member savedMember = invocation.getArgument(0);
            assertThat(savedMember.getPassword()).isEqualTo(updatedMember.getPassword());
            return updatedMember;
        });
        when(jwtUtil.generateToken(any(Member.class))).thenReturn("mocked-token");

        TokenResponse response = memberService.updateMember(request);

        assertThat(response.token()).isEqualTo("mocked-token");
        verify(jwtUtil).generateToken(existingMember);
    }

    @Test
    void updateMemberNotFoundException() {
        MemberRequest request = new MemberRequest(
            "notexisting@test.com",
            "password123",
            "USER"
        );
        when(memberRepository.findByEmail("notexisting@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.updateMember(request))
            .isInstanceOf(MemberNotFoundException.class)
            .hasMessage("Member(email: notexisting@test.com ) not found");
    }

    @Test
    void deleteMemberNormalCase() {
        String email = "test@test.com";
        Member member = new Member(
            1L,
            email,
            "password123",
            "USER"
        );
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        memberService.deleteMember(email);

        verify(memberRepository).deleteById(member.getId());
    }

    @Test
    void deleteMemberNotFoundException() {
        String email = "notexisting@test.com";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.deleteMember(email))
            .isInstanceOf(MemberNotFoundException.class)
            .hasMessage("Member(email: notexisting@test.com ) not found");
    }

    @Test
    void getMemberNormalCase() {
        String email = "test@test.com";
        Member member = new Member(
            1L,
            email,
            "password123",
            "USER"
        );
        MemberResponse expectedResponse = new MemberResponse(
            1L,
            "test@test.com",
            "password123",
            "USER"
        );
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        MemberResponse response = memberService.getMember(email);

        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void getMemberNotFoundException() {
        String email = "notexisting@test.com";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getMember(email))
            .isInstanceOf(MemberNotFoundException.class)
            .hasMessage("Member(email: notexisting@test.com ) not found");
    }

    @Test
    void getAllMembersNormalCase() {
        Member member = new Member(
            1L,
            "test@test.com",
            "password123",
            "USER"
        );
        MemberResponse expectedResponse = new MemberResponse(
            1L,
            "test@test.com",
            "password123",
            "USER"
        );
        when(memberRepository.findAll()).thenReturn(List.of(member));

        List<MemberResponse> responses = memberService.getAllMembers();

        assertThat(responses).containsExactly(expectedResponse);
    }

    @Test
    void getAllMembersPagedNormalCase() {
        List<Member> members = List.of(
            new Member("test1@test.com", "password", "USER"),
            new Member("test2@test.com", "password", "USER"),
            new Member("test3@test.com", "password", "USER"),
            new Member("test4@test.com", "password", "USER"),
            new Member("test5@test.com", "password", "USER")
        );
        Pageable pageable = PageRequest.of(0, 5);
        Page<Member> page = new PageImpl<>(members, pageable, 15L);
        when(memberRepository.findAll(pageable)).thenReturn(page);

        PaginationResponse<MemberResponse> response = memberService.getAllMembersPaged(pageable);

        assertThat(response.content()).hasSize(5);
        assertThat(response.totalPages()).isEqualTo(3);
        assertThat(response.currentPages()).isEqualTo(0);
        assertThat(response.content().getFirst().email()).isEqualTo("test1@test.com");
        verify(memberRepository).findAll(pageable);
    }

}
