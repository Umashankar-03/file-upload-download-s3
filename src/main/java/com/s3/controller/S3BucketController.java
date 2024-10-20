package com.s3.controller;


import com.s3.service.S3StorageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("api/storage")
public class S3BucketController {

private final S3StorageService s3StorageService;


    public S3BucketController(S3StorageService s3StorageService) {
        this.s3StorageService = s3StorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam String userName,
            @RequestParam String fileName,
            @RequestParam MultipartFile file) {
        try {
            s3StorageService.uploadFile(userName, fileName, file);
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> searchFile(
            @RequestParam String userName,
            @RequestParam String searchTerm
    ){
     List<String> files =  s3StorageService.searchFiles(userName, searchTerm);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String fileKey) {
        byte[] fileContent = s3StorageService.downloadFile(fileKey);

        String fileType = getFileType(fileKey);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileKey + "\"")
                .contentType(MediaType.parseMediaType(fileType)) // Set the content type
                .body(fileContent);
    }

    private String getFileType(String fileName) {
        if (fileName.endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF_VALUE;
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG_VALUE;
        } else if (fileName.endsWith(".png")) {
            return MediaType.IMAGE_PNG_VALUE;
        } else if (fileName.endsWith(".txt")) {
            return MediaType.TEXT_PLAIN_VALUE;
        }
        return MediaType.APPLICATION_OCTET_STREAM_VALUE; // Default to binary stream
    }

}
