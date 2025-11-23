package org.example.finalproject.exception;

public class OtpNotValidException extends RuntimeException {
    public OtpNotValidException(String message) {
        super(message);
    }
}