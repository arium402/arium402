package com.team.arium.admin.noncurr.exception;

public class NoncurrException extends RuntimeException {
    public NoncurrException(String message) {
        super(message);
    }
    
    public NoncurrException(String message, Throwable cause) {
        super(message, cause);
    }
}