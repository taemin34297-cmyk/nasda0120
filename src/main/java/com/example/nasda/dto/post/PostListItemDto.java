package com.example.nasda.dto.post;

import java.time.LocalDateTime;

public record PostListItemDto(
        Integer id,
        String title,
        String categoryName,
        String authorNickname,
        Integer viewCount,
        LocalDateTime createdAt
) {}