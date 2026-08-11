package com.grote.mediaingestion.audio.service;

import com.grote.common.enums.MediaType;
import com.grote.mediacatalog.service.MediaCatalogService;
import com.grote.mediaingestion.common.exception.InvalidFileException;
import com.grote.mediaingestion.common.exception.UploadFileException;
import com.grote.storage.integration.S3StorageIntegration;
import com.grote.mediaprocessing.service.MediaProcessingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AudioMediaService {

    private final S3StorageIntegration bucketIntegration;
    private final MediaCatalogService catalogService;
    private final MediaProcessingServiceImpl mediaProcessingService;

    private static final long MAX_AUDIO_SIZE = 50 * 1024 * 1024;   // 50MB
    private static final List<String> ALLOWED_AUDIO_TYPES =
            List.of("audio/mpeg", "audio/mp3", "audio/wav", "audio/ogg");

    public String storeMusic(MultipartFile file) {
        this.validateFile(file);
        UUID mediaId = UUID.randomUUID();

        var key = this.bucketIntegration.uploadFile(file, "audio", mediaId);

        this.catalogService.registerPending(mediaId, file.getOriginalFilename(), MediaType.AUDIO);
        this.mediaProcessingService.process(mediaId, key, MediaType.AUDIO);

        return key;
    }
    private void validateFile(MultipartFile file) {
        if (file.isEmpty() || file.getSize() == 0) throw new InvalidFileException("File invalid.");
        if (file.getSize() >= MAX_AUDIO_SIZE) throw new InvalidFileException("File with size too large.");
        if (!ALLOWED_AUDIO_TYPES.contains(file.getContentType())) throw new InvalidFileException("Invalid file type. Only audio files are allowed.");
    }

}
