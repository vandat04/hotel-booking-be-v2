package hotel_booking.exception;

import hotel_booking.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Handle Bean Validation Exceptions (@Valid on @RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ApiResponse<Object> response = ApiResponse.error("Validation failed", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 2. Handle @Validated (method-level) constraint violations
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldPath = violation.getPropertyPath().toString();
            String field = fieldPath.contains(".") ? fieldPath.substring(fieldPath.lastIndexOf('.') + 1) : fieldPath;
            errors.put(field, violation.getMessage());
        }
        ApiResponse<Object> response = ApiResponse.error("Validation failed", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 3. Handle unreadable/malformed JSON body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ApiResponse<Object> response = ApiResponse.error("Malformed JSON request or missing required fields");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 4. Handle missing required @RequestParam
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingParams(MissingServletRequestParameterException ex) {
        ApiResponse<Object> response = ApiResponse.error("Required parameter '" + ex.getParameterName() + "' is missing");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 5. Handle type mismatch in @RequestParam or @PathVariable
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Parameter '%s' should be of type '%s'",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        ApiResponse<Object> response = ApiResponse.error(message);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 6. Handle custom AppException (generic business exception)
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Object>> handleAppExceptions(AppException ex) {
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, ex.getStatus());
    }

    // 7. Handle ResourceNotFoundException → 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 8. Handle DuplicateDataException → 409
    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateDataException(DuplicateDataException ex) {
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // 9. Handle IllegalArgumentException → 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 10. Handle wrong HTTP method → 405
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        ApiResponse<Object> response = ApiResponse.error("HTTP method '" + ex.getMethod() + "' is not supported for this endpoint");
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // 11. Handle 404 when no handler found
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoHandlerFound(NoHandlerFoundException ex) {
        ApiResponse<Object> response = ApiResponse.error("API endpoint not found: " + ex.getRequestURL());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 12. Handle Spring Security AccessDeniedException → 403 Forbidden
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        ApiResponse<Object> response = ApiResponse.error("Access denied: You do not have permission to access this resource");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // 13. Handle Spring Security AuthenticationException → 401 Unauthorized
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(AuthenticationException ex) {
        ApiResponse<Object> response = ApiResponse.error("Unauthorized: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // 14. Catch-all → 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralExceptions(Exception ex) {
        ApiResponse<Object> response = ApiResponse.error("An unexpected error occurred: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
