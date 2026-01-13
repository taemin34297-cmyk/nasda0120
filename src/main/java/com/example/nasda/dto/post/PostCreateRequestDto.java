package com.example.nasda.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostCreateRequestDto(
        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 200, message = "제목은 최대 200자까지 가능합니다.")
        String title,

        @NotBlank(message = "카테고리는 필수입니다.")
        String category,

        @Size(max = 5000, message = "설명은 최대 5000자까지 가능합니다.")
        String description
) {}
