package com.grote.mediacatalog.repository;

import com.grote.mediacatalog.entity.MediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<MediaEntity, UUID> {
}