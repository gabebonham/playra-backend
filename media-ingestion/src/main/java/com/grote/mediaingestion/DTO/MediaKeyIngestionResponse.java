package com.grote.mediaingestion.DTO;

import lombok.Builder;

@Builder
public record MediaKeyIngestionResponse(
        String mediaKey
) {}
