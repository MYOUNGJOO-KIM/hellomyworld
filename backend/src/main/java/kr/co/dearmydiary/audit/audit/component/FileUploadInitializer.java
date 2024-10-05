package kr.co.dearmydiary.audit.audit.component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class FileUploadInitializer {
    @Value("${file.upload-dir}")
    private String uploadDir;

    // @PostConstruct
    // public void init() {
    //     File dir = new File(uploadDir);
    //     if (!dir.exists()) {
    //         dir.mkdirs();
    //     }
    // }

    @PostConstruct
    public void init() {
        Path path = Paths.get(uploadDir);
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }
}