package com.example.nasda.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentCreateRequestDto(
        @NotNull Integer postId,
        @NotBlank @Size(max = 500) String content
) {}
