package com.grote.common.config;

import com.grote.common.enums.MediaType;
import com.grote.processor.MediaProcessor;
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