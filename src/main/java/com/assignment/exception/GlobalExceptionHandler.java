package com.assignment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<String> handleNoteNotFoundExc(NoteNotFoundException notFoundException){
        return new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PermissionException.class)
    public ResponseEntity<String> handlePermissionExc(PermissionException permissionException){
        return new ResponseEntity<>(permissionException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidSearchFieldException.class)
    public ResponseEntity<String> handleInvalidSearchFieldExc(InvalidSearchFieldException invalidSearchFieldException){
        return new ResponseEntity<>(invalidSearchFieldException.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
