package com.grote.mediaingestion.audio.controller;

import com.grote.mediaingestion.audio.service.AudioMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/media-ingestion/audio")
@RequiredArgsConstructor
public class AudioMediaController {
    private final AudioMediaService audioMediaService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadMusic(@RequestParam("file") MultipartFile file) {
        String response = audioMediaService.storeMusic(file);
        return ResponseEntity.ofNullable(response);
    }
}
