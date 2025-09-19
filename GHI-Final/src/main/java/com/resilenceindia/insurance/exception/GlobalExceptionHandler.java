package com.resilenceindia.insurance.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.resilenceindia.insurance.dto.ApiResponse;
import com.resilenceindia.insurance.exception.GlobalExceptionHandler.BusinessException;
import com.resilenceindia.insurance.exception.GlobalExceptionHandler.ErrorResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(CustomerAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomerAlreadyExists(CustomerAlreadyExistsException ex) {
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomerNotFound(CustomerNotFoundException ex) {
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ApiResponse<Object> response = new ApiResponse<>(false, "Validation failed", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        ApiResponse<Object> response = ApiResponse.error("An unexpected error occurred: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    
 // 🔹 Handle custom "business" exceptions with error codes
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse error = new ErrorResponse(
            ex.getErrorCode(), // each exception will carry its own code
            ex.getMessage(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, ex.getStatus());
    }

    // 🔹 Handle unexpected runtime exceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred: " + ex.getMessage(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

//    // 🔹 Handle generic fallback
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleGenericException1(Exception ex) {
//        ErrorResponse error = new ErrorResponse(
//            "INTERNAL_SERVER_ERROR",
//            "An unexpected error occurred. Please contact support.",
//            LocalDateTime.now()
//        );
//        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    // 🔹 Inner class for structured error response
    public static class ErrorResponse {
        private String errorCode;
        private String message;
        private LocalDateTime timestamp;

        public ErrorResponse(String errorCode, String message, LocalDateTime timestamp) {
            this.errorCode = errorCode;
            this.message = message;
            this.timestamp = timestamp;
        }

        public String getErrorCode() { return errorCode; }
        public String getMessage() { return message; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }

    // 🔹 Custom exception wrapper (no need for multiple classes!)
    public static class BusinessException extends RuntimeException {
        private final String errorCode;
        private final HttpStatus status;

        public BusinessException(String errorCode, String message, HttpStatus status) {
            super(message);
            this.errorCode = errorCode;
            this.status = status;
        }

        public String getErrorCode() { return errorCode; }
        public HttpStatus getStatus() { return status; }
    }

}