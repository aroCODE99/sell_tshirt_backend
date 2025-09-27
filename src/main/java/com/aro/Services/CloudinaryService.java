package com.aro.Services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    private Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Uploads the file to the cloud and returns res ( But file null check is not handled, you have to explicitly handle it )
     * @param file
     * @param folderName
     * @return
     * @throws IOException
     */
    public Map<?, ?> uploadFile(MultipartFile file, String folderName) throws IOException {
        String publicId = UUID.randomUUID().toString();
        byte[] fileBytes = file.getBytes();
        return (Map<?, ?>) cloudinary.uploader().upload(
            fileBytes,
            ObjectUtils.asMap(
                "public_id", publicId,
                "resource_type", "auto",
                "folder", folderName
            )
        );
    }

    /**
     * Destroys the image file on the cloud with the public id
     * @param publicId
     * Taking the public id of the image as the parameter
     * @return Map<?, ?>
     * @throws IOException
     */
    public Map<?, ?> destroyFile(String publicId) throws IOException {
        return (Map<?, ?>) cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    public ResponseEntity<?> test() {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                "https://res.cloudinary.com/demo/image/upload/sample.jpg",
                ObjectUtils.emptyMap()
            );

            return ResponseEntity.ok("Cloudinary connection successful: " + result.get("url"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Cloudinary connection failed: " + e.getMessage());
        }
    }
}
