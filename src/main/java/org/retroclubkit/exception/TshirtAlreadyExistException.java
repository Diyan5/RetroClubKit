package org.retroclubkit.exception;

public class TshirtAlreadyExistException extends RuntimeException{

    public TshirtAlreadyExistException(String message) {
        super(message);
    }

    public TshirtAlreadyExistException() {
    }
}
