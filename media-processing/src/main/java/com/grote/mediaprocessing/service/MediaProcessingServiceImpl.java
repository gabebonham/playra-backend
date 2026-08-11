package com.grote.mediaprocessing.service;

import com.grote.common.enums.MediaType;
import com.grote.mediaprocessing.common.exception.UnsupportedMediaTypeException;
import com.grote.mediaprocessing.processor.MediaProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaProcessingServiceImpl implements MediaProcessingServiceInterface {

    private final Map<MediaType, MediaProcessor> mediaProcessors;

    @Override
    public void process(UUID mediaId, String bucketPath, MediaType type) {
        MediaProcessor processor = mediaProcessors.get(type);
        if (processor == null) {
            throw new UnsupportedMediaTypeException("No processor for type: " + type);
        }
        processor.process(mediaId, bucketPath);
    }

}