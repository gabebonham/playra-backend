package com.grote.mediaprocessing.processor.impl;

import com.grote.common.enums.MediaType;
import com.grote.mediacatalog.service.MediaCatalogService;
import com.grote.storage.integration.S3StorageIntegration;
import com.grote.mediaprocessing.processor.MediaProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AudioProcessor implements MediaProcessor {

    private final S3StorageIntegration storage;
    private final MediaCatalogService catalogService;

    @Override
    public MediaType getType() {
        return MediaType.AUDIO;
    }

    @Async
    @Override
    public void process(UUID mediaId, String bucketPath) {
        try {
            Path rawFile = storage.fetchToTempFile(bucketPath);
            Path outputDir = Files.createTempDirectory("hls-" + mediaId);

            this.runFfmpegHlsSegmentation(rawFile, outputDir);
            List<String> uploadedPaths = this.uploadAllSegments(outputDir, mediaId);

            String manifestPath = uploadedPaths.stream()
                    .filter(path -> path.endsWith(".m3u8"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Manifest not found among uploaded files"));

            this.catalogService.markAsAvailable(mediaId, manifestPath);

            this.cleanup(rawFile, outputDir);

        } catch (Exception e) {
            log.error("Failed to process media {}", mediaId, e);
            catalogService.markAsFailed(mediaId);
        }
    }

    private void runFfmpegHlsSegmentation(Path input, Path outputDir) throws IOException, InterruptedException {
        List<String> command = List.of(
                "ffmpeg", "-i", input.toString(),
                "-c:a", "aac", "-b:a", "128k",
                "-hls_time", "6",
                "-hls_segment_filename", outputDir + "/segment_%03d.ts",
                outputDir + "/playlist.m3u8"
        );

        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();

        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
            reader.lines().forEach(line -> log.debug("[ffmpeg] {}", line));
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg failed with code " + exitCode);
        }
    }

    private List<String> uploadAllSegments(Path outputDir, UUID mediaId) throws IOException {
        List<String> uploadedPaths = new ArrayList<>();
        try (var files = Files.list(outputDir)) {
            for (Path file : files.toList()) {
                String destination = "processed/" + mediaId + "/" + file.getFileName();
                uploadedPaths.add(storage.transferFile(file, destination));
            }
        }
        return uploadedPaths;
    }

    private void cleanup(Path rawFile, Path outputDir) {
        try {
            Files.deleteIfExists(rawFile);
            Files.walk(outputDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> p.toFile().delete());
        } catch (IOException e) {
            log.warn("Failed to clean up temp files", e);
        }
    }
}