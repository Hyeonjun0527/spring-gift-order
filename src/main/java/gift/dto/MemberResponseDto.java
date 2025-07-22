package gift.dto;

import gift.entity.Member;

public class MemberResponseDto {
    private Long id;
    private String email;

    public MemberResponseDto() {}

    public MemberResponseDto(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
    }

    public MemberResponseDto(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
} 