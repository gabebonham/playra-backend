package com.grote.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

@Getter
@AllArgsConstructor
public enum MediaType {

    AUDIO("audio"),
    VIDEO("video"),
    IMAGE("image");

    private final String name;

    static public MediaType fromValue(String value) {
        return EnumSet.allOf(MediaType.class).stream()
                .filter((mediaType -> mediaType.getName().equals(value)))
                .findFirst()
                .orElse(null);
    }
}
