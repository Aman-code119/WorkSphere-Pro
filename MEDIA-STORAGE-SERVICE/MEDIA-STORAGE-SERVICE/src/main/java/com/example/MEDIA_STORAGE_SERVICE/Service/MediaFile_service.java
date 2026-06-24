package com.example.MEDIA_STORAGE_SERVICE.Service;

import com.example.MEDIA_STORAGE_SERVICE.Model.MediaFile;
import com.example.MEDIA_STORAGE_SERVICE.Repository.MediaFile_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MediaFile_service {


    @Autowired
    private MediaFile_Repository mediaRepo;


    // Directory path jahan server par files store hongi
    private final String UPLOAD_DIR = "uploads/media/";

    // 1. FILE UPLOAD LOGIC: Local disk par save karna aur DB me metadata entry karna
    public MediaFile storeFile(MultipartFile file, Long referenceId, String fileType) {
        try {
            // Agar directory nahi bani, toh create karo
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // File duplication se bachne ke liye unique filename generate karna
            String originalFileName = file.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

            // File path mapping
            Path targetPath = Paths.get(UPLOAD_DIR + uniqueFileName);
            Files.copy(file.getInputStream(), targetPath);

            // DB Entity create karke save karna
            MediaFile mediaFile = new MediaFile();
            mediaFile.setReferenceId(referenceId);
            mediaFile.setFileType(fileType.toUpperCase());
            mediaFile.setFileName(originalFileName);
            // Dynamic absolute path ya local serving URL
            mediaFile.setFileStorageUrl(targetPath.toAbsolutePath().toString());
            mediaFile.setFileSize(file.getSize());
            mediaFile.setUploadedAt(LocalDateTime.now());

            return mediaRepo.save(mediaFile);
        } catch (Exception e) {
            throw new RuntimeException("Error: File upload fail ho gayi! " + e.getMessage());
        }
    }

    // 2. FETCH FILE METADATA: Meeting ID ke hisab se list nikalna
    public List<MediaFile> getFilesByReference(Long referenceId) {
        return mediaRepo.findByReferenceId(referenceId);
    }
}