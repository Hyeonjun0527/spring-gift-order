package gift.exception;

public class LastOptionException extends RuntimeException {
    public LastOptionException() {
        super("옵션은 반드시 1개 이상 존재해야합니다.");
    }
}
