package com.grote.app.common.exception;

import com.grote.app.common.ApiResponse;
import com.grote.common.exception.InvalidFileException;
import com.grote.delivery.exception.MediaNotReadyException;
import com.grote.mediacatalog.common.exception.MediaNotFoundException;
import com.grote.common.exception.WrongEnumValueException;
import com.grote.mediaingestion.common.exception.UploadFileException;
import com.grote.mediaprocessing.common.exception.UnsupportedMediaTypeException;
import com.grote.storage.common.exception.StorageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidFile(StorageException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }
    @ExceptionHandler(UnsupportedMediaTypeException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnsupportedMediaType(UnsupportedMediaTypeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }
    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidFile(InvalidFileException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(UploadFileException.class)
    public ResponseEntity<ApiResponse<Void>> handleFileUpload(UploadFileException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(ApiResponse.error(ex.getMessage()));
    }
    @ExceptionHandler(MediaNotReadyException.class)
    public ResponseEntity<ApiResponse<Void>> handleMediaNotReady(MediaNotReadyException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(ApiResponse.error(ex.getMessage()));
    }
    @ExceptionHandler(MediaNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleMediaNotReady(MediaNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }
    @ExceptionHandler(WrongEnumValueException.class)
    public ResponseEntity<ApiResponse<Void>> handleWrongEnumValue(WrongEnumValueException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

}