package com.jaywant.rentify.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> details = ex.getBindingResult()
                                  .getAllErrors()
                                  .stream()
                                  .map(error -> {
                                      String fieldName = ((FieldError) error).getField();
                                      String errorMessage = error.getDefaultMessage();
                                      return fieldName + ": " + errorMessage;
                                  })
                                  .collect(Collectors.toList());

        ValidationErrorResponse errorResponse = new ValidationErrorResponse("Validation Failed", details);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle other exceptions if necessary
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ValidationErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse("Internal Server Error", List.of(ex.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
