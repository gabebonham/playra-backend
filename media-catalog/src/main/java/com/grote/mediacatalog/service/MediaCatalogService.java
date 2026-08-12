package com.grote.mediacatalog.service;

import com.grote.common.enums.MediaType;
import com.grote.common.exception.InvalidFileException;
import com.grote.mediacatalog.DTO.UpdateMediaRequest;
import com.grote.mediacatalog.common.exception.MediaNotFoundException;
import com.grote.mediacatalog.entity.MediaEntity;
import com.grote.mediacatalog.enums.MediaStatus;
import com.grote.mediacatalog.repository.MediaRepository;
import com.grote.storage.integration.S3StorageIntegration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaCatalogService {

    private static final long MAX_COVER_SIZE = 25 * 1024 * 1024;   // 50MB
    private static final List<String> ALLOWED_COVER_TYPES =
            List.of("image/png", "image/jpeg");

    private final MediaRepository repository;
    private final S3StorageIntegration storage;

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
        MediaEntity media = this.getById(mediaId);
        media.setStatus(MediaStatus.AVAILABLE);
        media.setManifestPath(manifestPath);
        media.setProcessedAt(Instant.now());
        this.repository.save(media);
    }

    public void markAsFailed(UUID mediaId) {
        MediaEntity media = this.getById(mediaId);
        media.setStatus(MediaStatus.FAILED);
        this.repository.save(media);
    }

    public MediaEntity getById(UUID mediaId) {
        return this.repository.findById(mediaId)
                .orElseThrow(() -> new MediaNotFoundException("Media not found for id " + mediaId));
    }
    public Page<MediaEntity> getAll(int page, int size, String type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<MediaEntity> response;
        if (Objects.isNull(type)) {
            response = this.repository.findByStatus(MediaStatus.AVAILABLE, pageable);
        } else {
            response = this.repository.findByStatusAndType(MediaStatus.AVAILABLE, MediaType.fromValue(type), pageable);
        }
        return response;
    }
    public MediaEntity updateMediaContent(UUID id, UpdateMediaRequest request) {
        MediaEntity media = repository.findById(id)
                .orElseThrow(() -> new MediaNotFoundException("Media not found for id " + id));

        if (request.title() != null) {
            media.setTitle(request.title());
        }
        if (request.description() != null) {
            media.setDescription(request.description());
        }

        return repository.save(media);
    }
    public MediaEntity updateMediaCover(UUID mediaId, MultipartFile file) {
        this.validateFile(file, ALLOWED_COVER_TYPES, MAX_COVER_SIZE);
        MediaEntity media = this.getById(mediaId);

        String coverUrl = this.storage.uploadFile(file, "covers", mediaId);
        media.setImageUrl(coverUrl);

        return repository.save(media);
    }
    private void validateFile(MultipartFile file, List<String> allowedTypes, long fileSize) {
        if (file.isEmpty() || file.getSize() == 0) throw new InvalidFileException("File invalid.");
        if (file.getSize() >= fileSize) throw new InvalidFileException("File with size too large.");
        if (!allowedTypes.contains(file.getContentType())) throw new InvalidFileException("Invalid file type.");
    }
}