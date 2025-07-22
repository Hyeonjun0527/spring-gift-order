package gift.exception;

public class MemberNotFoundException extends RuntimeException {

    private final String keyName;
    private final String keyValue;

    public MemberNotFoundException(String keyName, String keyValue) {
        super(String.format("회원 정보를 찾을 수 없습니다. (%s: %s)", keyName, keyValue));
        this.keyName = keyName;
        this.keyValue = keyValue;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getKeyValue() {
        return keyValue;
    }
}