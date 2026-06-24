package com.example.MEDIA_STORAGE_SERVICE.Controller;

import com.example.MEDIA_STORAGE_SERVICE.Model.MediaFile;
import com.example.MEDIA_STORAGE_SERVICE.Service.MediaFile_service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
public class MediaFile_controller {
        @Autowired
        private MediaFile_service mediaService;

        // 1. ENDPOINT: File Upload karna (Recording, Presentation, Audit Logs)
        // URL: POST http://localhost:8085/api/media/upload
        @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<?> uploadMedia(@RequestParam("file") MultipartFile file,
                                             @RequestParam("referenceId") Long referenceId,
                                             @RequestParam("fileType") String fileType) {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Error: File khali hai, please valid file select karein."));
            }
            try {
                MediaFile uploadedFile = mediaService.storeFile(file, referenceId, fileType);
                return ResponseEntity.ok(Map.of(
                        "message", "File uploaded successfully to Corporate Disk!",
                        "fileId", uploadedFile.getId(),
                        "fileName", uploadedFile.getFileName(),
                        "storageUrl", uploadedFile.getFileStorageUrl()
                ));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
        }

        // 2. ENDPOINT: Kisi specific meeting ki saari files fetch karna
        // URL: GET http://localhost:8085/api/media/list?referenceId=12
        @GetMapping("/list")
        public ResponseEntity<?> getMediaList(@RequestParam Long referenceId) {
            try {
                List<MediaFile> files = mediaService.getFilesByReference(referenceId);
                return ResponseEntity.ok(files);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
        }
}