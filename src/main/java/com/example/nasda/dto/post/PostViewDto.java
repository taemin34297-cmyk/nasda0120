package com.example.nasda.dto.post;

import java.time.LocalDateTime;
import java.util.List;

public record PostViewDto(
        Integer id,
        String title,
        String content,
        String category,
        AuthorDto author,
        List<String> images,
        LocalDateTime createdAt,
        boolean isOwner
) {
    public record AuthorDto(String nickname) {}
}
