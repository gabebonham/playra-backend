package com.grote.mediaprocessing.processor.impl;

import com.grote.common.enums.MediaType;
import com.grote.storage.integration.S3StorageIntegration;
import com.grote.mediaprocessing.processor.MediaProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AudioProcessor implements MediaProcessor {
    private final S3StorageIntegration storage;

    @Override
    public MediaType getType() {
        return MediaType.AUDIO;
    }

    @Async
    @Override
    public void process(String bucketPath) {
        try {
            Path rawFile = this.storage.fetchToTempFile(bucketPath);
            Path outputDir = Files.createTempDirectory("hls-");

            this.runFfmpegHlsSegmentation(rawFile, outputDir);
            this.uploadAllSegments(outputDir);

            this.cleanup(rawFile, outputDir);

        } catch (Exception ignored) {
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

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg failed with code " + exitCode);
        }
    }

    private List<String> uploadAllSegments(Path outputDir) throws IOException {
        List<String> uploadedPaths = new ArrayList<>();
        try (var files = Files.list(outputDir)) {
            for (Path file : files.toList()) {
                String destination = "processed/" + "/" + file.getFileName();
                uploadedPaths.add(this.storage.transferFile(file, destination));
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
        } catch (IOException ignored) {
        }
    }
}
