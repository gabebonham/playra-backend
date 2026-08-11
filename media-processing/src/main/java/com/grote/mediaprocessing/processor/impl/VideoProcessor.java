package com.grote.mediaprocessing.processor.impl;

import com.grote.common.enums.MediaType;
import com.grote.mediaprocessing.processor.MediaProcessor;
import org.springframework.stereotype.Component;

@Component
public class VideoProcessor implements MediaProcessor {
    @Override
    public MediaType getType() {
        return MediaType.VIDEO;
    }
    @Override
    public void process(String bucketPath) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}