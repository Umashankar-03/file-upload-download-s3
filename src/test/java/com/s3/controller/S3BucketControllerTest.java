package com.s3.controller;

import com.s3.service.S3StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class S3BucketControllerTest {

    @InjectMocks
    private S3BucketController s3BucketController;

    @Mock
    private S3StorageService s3StorageService;

    @Mock
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadFileSuccess() throws Exception {
        // Arrange
        String userName = "testUser";
        String fileName = "testFile.txt";

        ResponseEntity<String> response = s3BucketController.uploadFile(userName, fileName, mockFile);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("File uploaded successfully.", response.getBody());
        verify(s3StorageService, times(1)).uploadFile(userName, fileName, mockFile);
    }

    @Test
    void testUploadFileFailure() throws Exception {

        String userName = "testUser";
        String fileName = "testFile.txt";
        doThrow(new RuntimeException("Upload failed")).when(s3StorageService).uploadFile(any(), any(), any());

        ResponseEntity<String> response = s3BucketController.uploadFile(userName, fileName, mockFile);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("File upload failed: Upload failed"));
    }

    @Test
    void testDownloadFile() throws IOException {

        String fileKey = "testUser/testFile.txt";
        byte[] fileContent = "Test content".getBytes();
        when(s3StorageService.downloadFile(fileKey)).thenReturn(fileContent);

        ResponseEntity<byte[]> response = s3BucketController.downloadFile(fileKey);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(fileContent, response.getBody());
        assertEquals("attachment; filename=\"" + fileKey + "\"", response.getHeaders().getFirst("Content-Disposition"));
    }

    @Test
    void testSearchFile() {

        String userName = "testUser";
        String searchTerm = "test";
        when(s3StorageService.searchFiles(userName, searchTerm)).thenReturn(List.of("testUser/testFile1.txt", "testUser/testFile2.txt"));

        ResponseEntity<List<String>> response = s3BucketController.searchFile(userName, searchTerm);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains("testUser/testFile1.txt"));
        assertTrue(response.getBody().contains("testUser/testFile2.txt"));
    }
}

