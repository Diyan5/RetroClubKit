package org.retroclubkit.exception;

public class TeamAlreadyExistException extends RuntimeException{

    public TeamAlreadyExistException(String message) {
        super(message);
    }

    public TeamAlreadyExistException() {
    }
}
