package org.retroclubkit.exception;

public class MustBePositiveException extends RuntimeException {

    public MustBePositiveException() {
    }

    public MustBePositiveException(String message) {
        super(message);
    }
}
