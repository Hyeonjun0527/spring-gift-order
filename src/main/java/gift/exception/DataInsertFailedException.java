package gift.exception;

public class DataInsertFailedException extends RuntimeException {
    public DataInsertFailedException() {
        super("데이터 저장에 실패했습니다.");
    }
}
