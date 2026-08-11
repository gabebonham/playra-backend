package com.grote.mediaprocessing.processor;

import com.grote.common.enums.MediaType;

import java.util.UUID;

public interface MediaProcessor {
    void process(UUID mediaId, String bucketPath);
    MediaType getType();
}