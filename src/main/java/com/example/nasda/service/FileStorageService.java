package com.example.nasda.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일입니다.");
        }
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }

        try {
            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);

            String original = file.getOriginalFilename() == null ? "image" : file.getOriginalFilename();
            String ext = "";

            int dot = original.lastIndexOf('.');
            if (dot > -1) ext = original.substring(dot); // .jpg

            String savedName = UUID.randomUUID() + ext;
            Path target = dir.resolve(savedName);

            // 덮어쓰기 방지
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // 브라우저 접근 URL
            return "/uploads/" + savedName;

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    public void deleteByUrl(String imageUrl) {
        // imageUrl: /uploads/xxx.jpg
        if (imageUrl == null || !imageUrl.startsWith("/uploads/")) return;

        try {
            String filename = imageUrl.substring("/uploads/".length());
            Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(filename);
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
            // 파일 삭제 실패는 DB 삭제보다 덜 치명적이므로 일단 무시(로그는 추후)
        }
    }
}
