package com.grote.mediaprocessing.service;


import com.grote.common.enums.MediaType;

import java.util.UUID;

public interface MediaProcessingServiceInterface {
    void process(UUID mediaId, String bucketPath, MediaType type);
}
