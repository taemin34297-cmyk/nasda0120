package com.example.nasda.dto.comment;

import java.time.LocalDateTime;

public record CommentViewDto(
        Integer id,
        String content,
        String authorNickname,
        LocalDateTime createdAt,
        boolean canEdit
) {
}