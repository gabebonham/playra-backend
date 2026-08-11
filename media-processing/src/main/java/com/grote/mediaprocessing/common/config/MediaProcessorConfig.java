package com.grote.mediaprocessing.common.config;

import com.grote.mediaprocessing.common.enums.MediaType;
import com.grote.mediaprocessing.processor.MediaProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class MediaProcessorConfig {

    @Bean
    public Map<MediaType, MediaProcessor> mediaProcessors(List<MediaProcessor> processors) {
        return processors.stream()
                .collect(Collectors.toMap(MediaProcessor::getType, Function.identity()));
    }
}