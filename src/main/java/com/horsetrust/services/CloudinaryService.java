package com.horsetrust.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Value("${app.cloudinary.cloud-name}")
    private String cloudName;

    @Value("${app.cloudinary.api-key}")
    private String apiKey;

    @Value("${app.cloudinary.api-secret}")
    private String apiSecret;

    @Value("${app.cloudinary.folder}")
    private String baseFolder;

    private Cloudinary cloudinary;

    @PostConstruct
    public void init() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    /**
     * Subidas de archivos generales como un MultiPartFile
     */
    public Map<String, Object> upload(MultipartFile file, String subfolder) throws IOException {
        String folderPath = baseFolder + "/" + subfolder;
        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "folder", folderPath,
                "resource_type", "auto" // permite imagenes, videos o pdfs
        );
        return cloudinary.uploader().upload(file.getBytes(), uploadParams);
    }

    /**
     * Elimina el archivo dada su publicId exacta referenciada en Cloudinary
     */
    public void delete(String publicId) throws IOException {
        if (publicId != null && !publicId.isBlank()) {
            Map<String, Object> destroyParams = ObjectUtils.asMap(
                    "invalidate", true,
                    "resource_type", "image" // Default para destroy, aunque si es raw hay que pasar parametro
            );
            cloudinary.uploader().destroy(publicId, destroyParams);
        }
    }
}
