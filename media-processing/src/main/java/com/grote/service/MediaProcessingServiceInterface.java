package com.grote.service;

import com.grote.common.enums.MediaType;


public interface MediaProcessingServiceInterface {
    void process(String bucketPath, String type);
}
