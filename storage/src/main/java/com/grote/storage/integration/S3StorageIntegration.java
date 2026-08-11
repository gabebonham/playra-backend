package com.grote.storage.integration;

import com.grote.storage.common.exception.StorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3StorageIntegration {

    private final S3Presigner presigner;

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String mediaFolder, UUID mediaId) {
        String extension = getExtension(file.getOriginalFilename());
        String key = mediaFolder + "/" + mediaId + extension;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .metadata(Map.of("original-filename", file.getOriginalFilename() != null ? file.getOriginalFilename() : ""))
                .build();

        try {
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new StorageException("Failed to upload file: " + key);
        }

        return key;
    }

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

    public String transferFile(Path localFile, String destinationPath) {
        try {
            s3Client.putObject(
                    PutObjectRequest.builder().bucket(bucketName).key(destinationPath).build(),
                    localFile
            );
            return destinationPath;
        } catch (Exception e) {
            throw new StorageException("Failed to upload processed file: " + destinationPath);
        }
    }

    public String generatePresignedUrl(String bucketPath, Duration expiration) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(bucketPath)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    public String deleteFile(String fileKey) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        s3Client.deleteObject(request);

        return fileKey;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.'));
    }
}