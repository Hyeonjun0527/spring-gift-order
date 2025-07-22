package gift.exception;

public class OptionNotExistException extends RuntimeException {
    public OptionNotExistException(Long optionId) {
      super("옵션 ID " + optionId + "에 해당하는 옵션이 존재하지 않습니다.");
    }
}
