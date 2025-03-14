package org.retroclubkit.exception;

public class EmailAlreadyExistException extends RuntimeException{

    public EmailAlreadyExistException(String message) {
        super(message);
    }

    public EmailAlreadyExistException() {
    }
}
