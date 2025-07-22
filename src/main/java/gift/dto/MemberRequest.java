package gift.dto;

public record MemberRequest(
    String email,
    String password,
    String role
) {

}
