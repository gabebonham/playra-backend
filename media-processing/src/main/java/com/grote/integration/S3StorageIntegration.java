package com.grote.integration;

import com.grote.common.exception.StorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class S3StorageIntegration {

    private final S3Client s3Client;

    @Value("${storage.bucket-name}")
    private String bucketName;

    public Path fetchToTempFile(String bucketPath) {
        try {
            Path tempFile = Files.createTempFile("media-", ".tmp");
            s3Client.getObject(
                    GetObjectRequest.builder().bucket(bucketName).key(bucketPath).build(),
                    tempFile
            );
            return tempFile;
        } catch (IOException e) {
            throw new StorageException("Failed to fetch file: " + bucketPath);
        }
    }

    public String uploadFile(Path localFile, String destinationPath) {
        s3Client.putObject(
                PutObjectRequest.builder().bucket(bucketName).key(destinationPath).build(),
                localFile
        );
        return destinationPath;
    }
}