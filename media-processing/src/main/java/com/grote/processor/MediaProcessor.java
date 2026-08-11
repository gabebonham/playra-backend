package com.grote.processor;

import com.grote.common.enums.MediaType;

public interface MediaProcessor {
    void process(String bucketPath);
    MediaType getType();
}