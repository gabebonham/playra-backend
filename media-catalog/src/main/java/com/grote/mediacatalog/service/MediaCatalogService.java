package com.grote.mediacatalog.service;

import com.grote.common.enums.MediaType;
import com.grote.mediacatalog.common.exception.MediaNotFoundException;
import com.grote.mediacatalog.entity.MediaEntity;
import com.grote.mediacatalog.enums.MediaStatus;
import com.grote.mediacatalog.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaCatalogService {

    private final MediaRepository repository;

    public String getManifestPath(UUID id) {
        var media = this.repository.findById(id)
                .orElseThrow(() -> new MediaNotFoundException("Media not found for id " + id));

        return media.getManifestPath();
    }

    public void registerPending(UUID mediaId, String fileOriginalName, MediaType type) {
        MediaEntity media = MediaEntity.builder()
                .id(mediaId)
                .title(fileOriginalName)
                .type(type)
                .status(MediaStatus.PENDING)
                .createdAt(Instant.now())
                .build();

        this.repository.save(media);
    }

    public void markAsAvailable(UUID mediaId, String manifestPath) {
        MediaEntity media = getOrThrow(mediaId);
        media.setStatus(MediaStatus.AVAILABLE);
        media.setManifestPath(manifestPath);
        media.setProcessedAt(Instant.now());
        this.repository.save(media);
    }

    public void markAsFailed(UUID mediaId) {
        MediaEntity media = getOrThrow(mediaId);
        media.setStatus(MediaStatus.FAILED);
        this.repository.save(media);
    }

    private MediaEntity getOrThrow(UUID mediaId) {
        return this.repository.findById(mediaId)
                .orElseThrow(() -> new MediaNotFoundException("Media not found for id " + mediaId));
    }
}