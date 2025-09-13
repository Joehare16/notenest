package com.nestenote.notenest.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    //if this error occurs call this class to handle the api error output
    //takes in "ex" the exception thrown and the request details
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> hanndleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest req){
         List<String> details = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            //get name of the failed field and the message from DTO
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .toList();

        ApiError error = new ApiError(
            //http error code
            HttpStatus.BAD_REQUEST.value()
            //error message
            ,"Validation Failed", 
            //path of API where error is from
            req.getRequestURI(), 
            //list of bad fields and DTO error i.e title:" title cannot be empty"
            details);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError>handleContraintViolation(ConstraintViolationException ex, HttpServletRequest req){
        List<String> details = ex.getConstraintViolations()
        .stream()
        .map(v -> v.getPropertyPath() + ":" + v.getMessage())
        .toList();

        ApiError error = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            req.getRequestURI(),
            details);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleBadJson(HttpMessageNotReadableException ex, HttpServletRequest req)
        {
            ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Malformed JSON Request",
                req.getRequestURI(),
                List.of(ex.getMessage())
            );

            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleCustomValidation(ValidationException ex, HttpServletRequest req)
    {
        ApiError error = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            req.getRequestURI(), 
            List.of(ex.getMessage())
            );
        
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<ApiError> handleNoteNotFound(NoteNotFoundException ex, HttpServletRequest req) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "Resource not found",
                req.getRequestURI(),
                List.of(ex.getMessage())
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
     @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAnyOtherException(Exception ex, HttpServletRequest req) {
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unexpected error",
                req.getRequestURI(),
                List.of("Something went wrong. Please try again later.")
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req)
        {
             ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ivalid login credentials",
                req.getRequestURI(),
                List.of(ex.getMessage())
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
}
