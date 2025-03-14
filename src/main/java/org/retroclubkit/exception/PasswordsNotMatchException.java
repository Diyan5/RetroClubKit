package org.retroclubkit.exception;

public class PasswordsNotMatchException extends RuntimeException{

    public PasswordsNotMatchException(String message) {
        super(message);
    }

    public PasswordsNotMatchException() {
    }
}
