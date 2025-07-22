package gift.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ProblemDetail> handleUnauthorized(UnauthorizedException ex, HttpServletRequest req) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        ProblemDetail body = ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), ex.getMessage());
        body.setProperty("code", errorCode.name());
        body.setProperty("path", req.getRequestURI());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ProblemDetail> handleForbidden(ForbiddenException ex, HttpServletRequest req) {
        ErrorCode errorCode = ErrorCode.FORBIDDEN;
        ProblemDetail body = ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), ex.getMessage());
        body.setProperty("code", errorCode.name());
        body.setProperty("path", req.getRequestURI());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBind(BindException ex, HttpServletRequest req) {
        List<FieldErrorResponse> fieldErrors = createFieldErrorDetails(ex);

        ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;
        ProblemDetail body = ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), errorCode.getMessage());
        body.setProperty("code", errorCode.name());
        body.setProperty("errors", fieldErrors);
        body.setProperty("path", req.getRequestURI());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        List<FieldErrorResponse> fieldErrors = createFieldErrorDetails(ex);

        ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;
        ProblemDetail body = ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), errorCode.getMessage());
        body.setProperty("code", errorCode.name());
        body.setProperty("errors", fieldErrors);
        body.setProperty("path", request.getDescription(false).replace("uri=", ""));
        
        return handleExceptionInternal(ex, body, headers, errorCode.getHttpStatus(), request);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ProblemDetail> handleNpe(NullPointerException ex, HttpServletRequest req) {
        ErrorCode errorCode = ErrorCode.REQUIRED_VALUE_MISSING;
        ProblemDetail body = ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), errorCode.getMessage());
        body.setProperty("code", errorCode.name());
        body.setProperty("path", req.getRequestURI());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArg(IllegalArgumentException ex, HttpServletRequest req) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ProblemDetail body = ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), ex.getMessage());
        body.setProperty("code", errorCode.name());
        body.setProperty("path", req.getRequestURI());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> handleNoSuchElement(NoSuchElementException ex, HttpServletRequest req) {
        ErrorCode errorCode = ErrorCode.RESOURCE_NOT_FOUND;
        ProblemDetail body = ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), errorCode.getMessage());
        body.setProperty("code", errorCode.name());
        body.setProperty("path", req.getRequestURI());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ProblemDetail> handleNumberFormat(NumberFormatException ex, HttpServletRequest req) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ProblemDetail body = ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), "올바르지 않은 숫자 형식입니다.");
        body.setProperty("code", errorCode.name());
        body.setProperty("path", req.getRequestURI());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        String detail = String.format("'%s' 파라미터 값 '%s'는 올바른 타입이 아닙니다.", ex.getName(), ex.getValue());
        ProblemDetail body = ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), detail);
        body.setProperty("code", errorCode.name());
        body.setProperty("field", ex.getName());
        body.setProperty("rejectedValue", ex.getValue());
        body.setProperty("path", req.getRequestURI());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ErrorCode errorCode = ErrorCode.MISSING_REQUIRED_PARAMETER;
        ProblemDetail body = ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), errorCode.getMessage());
        body.setProperty("code", errorCode.name());
        body.setProperty("parameter", ex.getParameterName());
        body.setProperty("path", request.getDescription(false).replace("uri=", ""));

        return handleExceptionInternal(ex, body, headers, errorCode.getHttpStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ErrorCode errorCode = ErrorCode.INVALID_REQUEST_FORMAT;
        ProblemDetail body = ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), errorCode.getMessage());
        body.setProperty("code", errorCode.name());
        body.setProperty("path", request.getDescription(false).replace("uri=", ""));

        return handleExceptionInternal(ex, body, headers, errorCode.getHttpStatus(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex, HttpServletRequest req) {
        ErrorCode errorCode = ErrorCode.UNEXPECTED_ERROR;
        ProblemDetail body = ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), errorCode.getMessage());
        body.setProperty("code", errorCode.name());
        body.setProperty("path", req.getRequestURI());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
    }

    private List<FieldErrorResponse> createFieldErrorDetails(BindException ex) {
        return ex.getFieldErrors().stream()
                .map(fe -> new FieldErrorResponse(
                        fe.getField(),
                        fe.getRejectedValue(),
                        fe.getDefaultMessage()
                ))
                .toList();
    }
}
