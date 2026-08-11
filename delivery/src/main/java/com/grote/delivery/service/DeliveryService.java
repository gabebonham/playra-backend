package com.grote.delivery.service;

import com.grote.delivery.exception.MediaNotReadyException;
import com.grote.mediacatalog.service.MediaCatalogService;
import com.grote.storage.integration.S3StorageIntegration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final MediaCatalogService catalogClient;
    private final S3StorageIntegration storage;

    public String getStreamUrl(UUID mediaId) {
        String manifestPath = catalogClient.getManifestPath(mediaId);

        if (Objects.isNull(manifestPath)) {
            throw new MediaNotReadyException("Media not yet processed: " + mediaId);
        }

        return storage.generatePresignedUrl(manifestPath, Duration.ofHours(2));
    }
}