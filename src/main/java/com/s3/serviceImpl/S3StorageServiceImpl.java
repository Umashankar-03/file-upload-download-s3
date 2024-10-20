package com.s3.serviceImpl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.s3.service.S3StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3StorageServiceImpl implements S3StorageService {

private final AmazonS3 amazonS3;
private final String bucketName ="user-storage-bucket";

    public S3StorageServiceImpl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }


    @Override
    public void uploadFile(String userName, String fileName, MultipartFile file) {
        String fileKey = userName + "/" + fileName;
        try {
            amazonS3.putObject(
                    new PutObjectRequest(bucketName, fileKey, file.getInputStream(), new ObjectMetadata())
            );
        } catch (Exception e) {
             throw new IllegalStateException("Error while uploading file to S3: " + e.getMessage(), e);
        }


    }

    @Override
    public List<String> searchFiles(String userName, String searchTerm) {
        String userFolder = userName +"/";

        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(userFolder);

        ListObjectsV2Result result = amazonS3.listObjectsV2(request);

        return result.getObjectSummaries().stream()
                .map(S3ObjectSummary :: getKey)
                .filter(key -> key.contains(searchTerm))
                .collect(Collectors.toList());
    }

    @Override
    public byte[] downloadFile(String fileKey) {
        S3Object s3Object = amazonS3.getObject(bucketName, fileKey);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read S3 object content", e);
        }
    }

}
