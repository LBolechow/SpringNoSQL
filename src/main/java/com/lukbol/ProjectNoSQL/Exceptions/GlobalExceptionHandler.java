package com.lukbol.ProjectNoSQL.Exceptions;

import com.lukbol.ProjectNoSQL.DTOs.ErrorMessageDTO;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_MSG_PREFIX = "Error message: ";

    @ExceptionHandler(ApplicationException.UserWithEmailAlreadyExistsException.class)
    public ResponseEntity<ErrorMessageDTO> handleEmailExists(ApplicationException.UserWithEmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageDTO(ERROR_MSG_PREFIX, ex.getMessage()));
    }

    @ExceptionHandler(ApplicationException.UserWithUsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorMessageDTO> handleUsernameExists(ApplicationException.UserWithUsernameAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageDTO(ERROR_MSG_PREFIX, ex.getMessage()));
    }

    @ExceptionHandler(ApplicationException.UserWithPhoneNumberAlreadyExistsException.class)
    public ResponseEntity<ErrorMessageDTO> handlePhoneExists(ApplicationException.UserWithPhoneNumberAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageDTO(ERROR_MSG_PREFIX, ex.getMessage()));
    }

    @ExceptionHandler(ApplicationException.UserNotFoundException.class)
    public ResponseEntity<ErrorMessageDTO> handleUserNotFound(ApplicationException.UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageDTO(ERROR_MSG_PREFIX, ex.getMessage()));
    }

    @ExceptionHandler(ApplicationException.InvalidPasswordException.class)
    public ResponseEntity<ErrorMessageDTO> handleInvalidPassword(ApplicationException.InvalidPasswordException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageDTO(ERROR_MSG_PREFIX, ex.getMessage()));
    }

    @ExceptionHandler(ApplicationException.PasswordsDoNotMatchException.class)
    public ResponseEntity<ErrorMessageDTO> handlePasswordsDoNotMatch(ApplicationException.PasswordsDoNotMatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageDTO(ERROR_MSG_PREFIX, ex.getMessage()));
    }

    @ExceptionHandler(ApplicationException.EmptyFieldException.class)
    public ResponseEntity<ErrorMessageDTO> handleEmptyField(ApplicationException.EmptyFieldException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageDTO(ERROR_MSG_PREFIX, ex.getMessage()));
    }

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class, DisabledException.class})
    public ResponseEntity<ErrorMessageDTO> handleAuthExceptions(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorMessageDTO(ERROR_MSG_PREFIX, ex.getMessage()));
    }
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorMessageDTO> handleDatabaseException(DataAccessException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessageDTO(ERROR_MSG_PREFIX,  ex.getMessage()));
    }
}