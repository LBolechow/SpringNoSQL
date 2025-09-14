package com.lukbol.ProjectNoSQL.Exceptions;


public class ApplicationException {

    public static class UserWithEmailAlreadyExistsException extends RuntimeException {
        public UserWithEmailAlreadyExistsException(String message) { super(message); }
    }

    public static class UserWithUsernameAlreadyExistsException extends RuntimeException {
        public UserWithUsernameAlreadyExistsException(String message) { super(message); }
    }

    public static class UserWithPhoneNumberAlreadyExistsException extends RuntimeException {
        public UserWithPhoneNumberAlreadyExistsException(String message) { super(message); }
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) { super(message); }
    }

    public static class InvalidPasswordException extends RuntimeException {
        public InvalidPasswordException(String message) { super(message); }
    }

    public static class PasswordsDoNotMatchException extends RuntimeException {
        public PasswordsDoNotMatchException(String message) { super(message); }
    }

    public static class EmptyFieldException extends RuntimeException {
        public EmptyFieldException(String message) { super(message); }
    }

}
