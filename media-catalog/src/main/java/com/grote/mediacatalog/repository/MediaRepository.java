package com.grote.mediacatalog.repository;

import com.grote.common.enums.MediaType;
import com.grote.mediacatalog.entity.MediaEntity;
import com.grote.mediacatalog.enums.MediaStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<MediaEntity, UUID> {
    Page<MediaEntity> findByStatusAndType(MediaStatus status, MediaType type, Pageable pageable);
    Page<MediaEntity> findByStatus(MediaStatus status, Pageable pageable);
}