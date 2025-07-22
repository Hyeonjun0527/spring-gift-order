package gift.exception;

public class DuplicateMemberException extends RuntimeException {
    public DuplicateMemberException(String email) {

        super(email + "은 이미 존재하는 이메일입니다.");
    }
}
