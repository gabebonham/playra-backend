package com.grote.mediaprocessing.processor.impl;

import com.grote.common.enums.MediaType;
import com.grote.mediaprocessing.processor.MediaProcessor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VideoProcessor implements MediaProcessor {
    @Override
    public MediaType getType() {
        return MediaType.VIDEO;
    }
    @Override
    public void process(UUID mediaId, String bucketPath) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}