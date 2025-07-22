package gift.controller;

import gift.config.JwtUtil;
import gift.config.LoginMember;
import gift.dto.MemberRequest;
import gift.dto.MemberResponse;
import gift.dto.PaginationResponse;
import gift.dto.TokenResponse;
import gift.entity.Member;
import gift.service.MemberService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    public MemberController(MemberService memberService, JwtUtil jwtUtil) {
        this.memberService = memberService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody MemberRequest request) {
        TokenResponse response = memberService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody MemberRequest request) {
        TokenResponse response = memberService.login(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<TokenResponse> updateMember(
        @RequestBody MemberRequest memberRequest,
        @LoginMember Member member
    ) {
        TokenResponse response = memberService.updateMember(memberRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteMember(
        @PathVariable String email,
        @LoginMember Member member
    ) {
        memberService.deleteMember(email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{email}")
    public ResponseEntity<MemberResponse> getMember(
        @PathVariable String email,
        @LoginMember Member member
    ) {
        MemberResponse response = memberService.getMember(email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public PaginationResponse<MemberResponse> getAllMembersPaged(
        @LoginMember
        Member member,
        @PageableDefault(size = 10, sort = "email")
        Pageable pageable
    ) {
        return memberService.getAllMembersPaged(pageable);
    }

}
