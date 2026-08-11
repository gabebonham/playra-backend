package com.grote.audio.service;

import com.grote.common.exception.InvalidFileException;
import com.grote.common.exception.UploadFileException;
import com.grote.integration.S3StorageIntegration;
import com.grote.service.MediaProcessingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AudioMediaService {

    private final S3StorageIntegration bucketIntegration;
    private final MediaProcessingServiceImpl mediaProcessingService;

    private static final long MAX_AUDIO_SIZE = 50 * 1024 * 1024;   // 50MB
    private static final List<String> ALLOWED_AUDIO_TYPES =
            List.of("audio/mpeg", "audio/mp3", "audio/wav", "audio/ogg");

    public String storeMusic(MultipartFile file) {
        this.validateFile(file);
        try {
            var key = this.bucketIntegration.uploadFile(file, "audio");
            this.mediaProcessingService.process(key, "audio");
            return key;
        } catch (IOException e) {
            throw new UploadFileException("Failed to upload file.");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty() || file.getSize() == 0) throw new InvalidFileException("File invalid.");
        if (file.getSize() >= MAX_AUDIO_SIZE) throw new InvalidFileException("File with size too large.");
        if (!ALLOWED_AUDIO_TYPES.contains(file.getContentType())) throw new InvalidFileException("Invalid file type. Only audio files are allowed.");
    }

}
