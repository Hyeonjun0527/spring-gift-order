package gift.exception;

public class DuplicateOptionNameException extends RuntimeException {
    public DuplicateOptionNameException(String optionName) {
      super(optionName + "은(는) 이미 존재하는 옵션입니다.");
    }
}
