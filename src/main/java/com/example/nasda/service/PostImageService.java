package com.example.nasda.service;

import com.example.nasda.domain.PostEntity;
import com.example.nasda.domain.PostImageEntity;
import com.example.nasda.repository.PostImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostImageService {

    private final PostImageRepository postImageRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public void addImages(PostEntity post, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return;

        int order = 0;
        boolean first = true;

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;

            String url = fileStorageService.saveImage(file);

            postImageRepository.save(
                    PostImageEntity.builder()
                            .post(post)
                            .imageUrl(url)
                            .sortOrder(order++)
                            .isRepresentative(first)
                            .build()
            );

            first = false;
        }
    }

    @Transactional
    public void replaceImages(Integer postId, PostEntity post, List<MultipartFile> newFiles) {

        boolean hasNew = newFiles != null && newFiles.stream().anyMatch(f -> f != null && !f.isEmpty());
        if (!hasNew) return;

        // 기존 이미지 파일 삭제
        List<PostImageEntity> oldImages =
                postImageRepository.findByPost_PostIdOrderBySortOrderAsc(postId);

        for (PostImageEntity img : oldImages) {
            fileStorageService.deleteByUrl(img.getImageUrl());
        }

        // DB 삭제
        postImageRepository.deleteByPost_PostId(postId);

        // 새 이미지 저장
        addImages(post, newFiles);
    }

    @Transactional(readOnly = true)
    public List<String> getImageUrls(Integer postId) {
        return postImageRepository.findByPost_PostIdOrderBySortOrderAsc(postId)
                .stream()
                .map(PostImageEntity::getImageUrl)
                .toList();
    }
}
