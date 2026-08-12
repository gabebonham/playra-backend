package com.grote.common.exception;

public class WrongEnumValueException extends RuntimeException {
    public WrongEnumValueException(String message) {
        super(message);
    }
}
