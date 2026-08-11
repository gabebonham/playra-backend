package com.grote.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3StorageIntegration {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String mediaFolder) throws IOException {
        String extension = getExtension(file.getOriginalFilename());
        String key = mediaFolder + "/" + UUID.randomUUID() + extension;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .metadata(Map.of("original-filename", file.getOriginalFilename() != null ? file.getOriginalFilename() : ""))
                .build();

        s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return key;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.'));
    }

    public String deleteFile(String fileKey, String mediaFolder) {
        String finalBucketName = this.bucketName + "/" + mediaFolder;

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(finalBucketName)
                .key(fileKey)
                .build();

        this.s3Client.deleteObject(request);

        return fileKey;
    }
}