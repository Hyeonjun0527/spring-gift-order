package gift.common.exception;

public record FieldErrorResponse(
    String field,
    Object rejectedValue,
    String message
) {
} 