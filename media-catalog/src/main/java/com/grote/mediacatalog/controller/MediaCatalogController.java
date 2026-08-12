package com.grote.mediacatalog.controller;

import com.grote.mediacatalog.DTO.MediaResponse;
import com.grote.mediacatalog.DTO.UpdateMediaRequest;
import com.grote.mediacatalog.service.MediaCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("api/media-catalog")
@RequiredArgsConstructor
public class MediaCatalogController {

    private final MediaCatalogService catalogService;

    @GetMapping("{id}")
    public ResponseEntity<MediaResponse> getMediaById(@PathVariable UUID id) {
        var media = this.catalogService.getById(id);
        return ResponseEntity.ok(MediaResponse.from(media));
    }
    @GetMapping
    public ResponseEntity<Page<MediaResponse>> getMedia(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "20") int size,
                                                        @RequestParam(required = false) String type) {
        var mediaPage = this.catalogService.getAll(page,size, type);
        return ResponseEntity.ok(mediaPage.map(MediaResponse::from));
    }
    @PatchMapping("{id}/content")
    public ResponseEntity<MediaResponse> updateMediaContent(@PathVariable UUID id, @RequestBody UpdateMediaRequest request) {
        var media = this.catalogService.updateMediaContent(id, request);
        return ResponseEntity.ok(MediaResponse.from(media));
    }
    @PatchMapping("{id}/cover")
    public ResponseEntity<MediaResponse> updateMediaCover(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
        var media = this.catalogService.updateMediaCover(id, file);
        return ResponseEntity.ok(MediaResponse.from(media));
    }
}
