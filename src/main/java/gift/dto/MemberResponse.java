package gift.dto;

public record MemberResponse(
    Long id,
    String email,
    String password,
    String role
) {

}
