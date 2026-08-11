package com.grote.mediacatalog.common.exception;

public class MediaNotFoundException extends RuntimeException{
    public MediaNotFoundException(String message) {
        super(message);
    }
}
