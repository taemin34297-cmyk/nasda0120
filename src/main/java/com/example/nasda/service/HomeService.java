package com.example.nasda.service;

import com.example.nasda.domain.PostImageEntity;
import com.example.nasda.dto.post.HomePostDto;
import com.example.nasda.repository.PostImageRepository;
import com.example.nasda.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;

    @Transactional(readOnly = true)
    public List<HomePostDto> getHomePosts() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(post -> {
                    String imageUrl = postImageRepository
                            .findFirstByPost_PostIdOrderBySortOrderAsc(post.getPostId())
                            .map(PostImageEntity::getImageUrl)
                            .orElse(null);

                    return new HomePostDto(
                            post.getPostId(),
                            post.getTitle(),
                            imageUrl
                    );
                })
                .toList();
    }
}
