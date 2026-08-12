package com.grote.mediacatalog.DTO;

import com.grote.common.enums.MediaType;
import com.grote.mediacatalog.entity.MediaEntity;
import com.grote.mediacatalog.enums.MediaStatus;

import java.time.Instant;
import java.util.UUID;

public record MediaResponse(
        UUID id,
        String title,
        String description,
        String imageUrl,
        MediaType type,
        MediaStatus status,
        String manifestPath,
        Instant createdAt
) {
    public static MediaResponse from(MediaEntity entity) {
        return new MediaResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getImageUrl(),
                entity.getType(),
                entity.getStatus(),
                entity.getManifestPath(),
                entity.getCreatedAt()
        );
    }
}