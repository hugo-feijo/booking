package com.hostfully.interview.exception;

public class DtoNotValidException extends RuntimeException{
    public DtoNotValidException(String message) {
        super(message);
    }
}
