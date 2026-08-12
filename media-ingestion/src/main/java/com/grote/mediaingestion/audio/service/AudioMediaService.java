package com.grote.mediaingestion.audio.service;

import com.grote.common.enums.MediaType;
import com.grote.mediacatalog.service.MediaCatalogService;
import com.grote.common.exception.InvalidFileException;
import com.grote.storage.integration.S3StorageIntegration;
import com.grote.mediaprocessing.service.MediaProcessingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public String storeMusic(MultipartFile mediaFile) {
        this.validateFile(mediaFile, ALLOWED_AUDIO_TYPES, MAX_AUDIO_SIZE);

        UUID mediaId = UUID.randomUUID();

        var mediaKey = this.bucketIntegration.uploadFile(mediaFile, "audio", mediaId);

        this.catalogService.registerPending(mediaId, mediaFile.getOriginalFilename(), MediaType.AUDIO);
        this.mediaProcessingService.process(mediaId, mediaKey, MediaType.AUDIO);

        return mediaKey;
    }
    private void validateFile(MultipartFile file, List<String> allowedTypes, long fileSize) {
        if (file.isEmpty() || file.getSize() == 0) throw new InvalidFileException("File invalid.");
        if (file.getSize() >= fileSize) throw new InvalidFileException("File with size too large.");
        if (!allowedTypes.contains(file.getContentType())) throw new InvalidFileException("Invalid file type.");
    }
}
