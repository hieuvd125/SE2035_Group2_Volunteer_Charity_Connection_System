package com.group2.volunteer.exception;

public class InvalidProjectStateException extends RuntimeException {
    public InvalidProjectStateException(String message) {
        super(message);
    }
}
