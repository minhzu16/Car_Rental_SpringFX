package org.exception;

public class ResourceUnsupportedException extends RuntimeException {
    public ResourceUnsupportedException(String message) {
        super(message);
    }
}