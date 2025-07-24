package gift.common.validation;

public final class ValidationMessages {

    //키워드
    public static final String NAME_KEYWORD_SIZE = "상품명 길이";
    public static final String NAME_KEYWORD_PATTERN = "상품명 패턴";
    public static final String NAME_KEYWORD_KAKAO = "카카오";
    public static final String PRICE_KEYWORD_MIN = "상품 가격 최소값";
    public static final String KEYWORD_NOT_BLANK = "공백 불가";
    //메세지
    public static final String NAME_SIZE_MESSAGE = NAME_KEYWORD_SIZE + ": 상품명은 최대 15자까지 입력할 수 있습니다.";
    public static final String NAME_PATTERN_MESSAGE = NAME_KEYWORD_PATTERN + ": 상품명에는 허용되지 않는 특수문자가 포함되어 있습니다.";
    public static final String NAME_KAKAO_MESSAGE = NAME_KEYWORD_KAKAO + ": '카카오'가 포함된 상품명은 담당 MD와 협의 후 사용 가능합니다.";
    public static final String PRICE_MIN_MESSAGE = PRICE_KEYWORD_MIN + ": 상품 가격은 1원 이상이어야 합니다.";
    public static final String PRICE_NOT_NULL_MESSAGE = "상품 가격은 필수 입력값입니다.";
    public static final String NOT_BLANK_MESSAGE = KEYWORD_NOT_BLANK + ": 필수 입력값은 공백일 수 없습니다.";
    private ValidationMessages() {}
} 