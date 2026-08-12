package com.grote.mediacatalog.DTO;

import lombok.Builder;

@Builder
public record UpdateMediaRequest(
        String title,
        String description
) {}