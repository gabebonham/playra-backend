package com.grote.delivery.controller;

import com.grote.delivery.dto.StreamResponseDTO;
import com.grote.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/{mediaId}/stream")
    public ResponseEntity<StreamResponseDTO> getStreamUrl(@PathVariable UUID mediaId) {
        String url = deliveryService.getStreamUrl(mediaId);
        return ResponseEntity.ok(new StreamResponseDTO(url));
    }
}