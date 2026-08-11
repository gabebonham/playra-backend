package com.grote.service;

import com.grote.common.enums.MediaType;
import com.grote.common.exception.UnsupportedMediaTypeException;
import com.grote.processor.MediaProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaProcessingServiceImpl implements MediaProcessingServiceInterface {

    private final Map<MediaType, MediaProcessor> mediaProcessors;

    @Override
    public void process(String bucketPath, String type) {

        MediaProcessor processor = this.mediaProcessors.get(MediaType.fromValue(type));
        if (processor == null) {
            throw new UnsupportedMediaTypeException("No processor for media type: " + type);
        }

        processor.process(bucketPath);
    }

}