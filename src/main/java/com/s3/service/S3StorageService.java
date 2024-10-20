package com.s3.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3StorageService {
    void uploadFile(String userName, String fileName, MultipartFile file);

    List<String> searchFiles(String userName, String searchTerm);

    byte[] downloadFile(String fileKey);
}
