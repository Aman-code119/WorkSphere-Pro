package com.example.MEDIA_STORAGE_SERVICE.Repository;

import com.example.MEDIA_STORAGE_SERVICE.Model.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaFile_Repository extends JpaRepository<MediaFile,Long> {
    // 🔍 Kisi specific meeting/appointment ki saari media files nikalna
    List<MediaFile> findByReferenceId(Long referenceId);

    // 🔍 File type ke hisab se filter karna (e.g., sirf RECORDING chahiye ya sirf PDF)
    List<MediaFile> findByReferenceIdAndFileType(Long referenceId, String fileType);
}
