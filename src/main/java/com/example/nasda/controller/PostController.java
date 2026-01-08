package com.example.nasda.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Controller
//@RequestMapping("/post")
public class PostController {

    // 👇 주소를 원하시는 대로 변경했습니다!
    @GetMapping("/post/view.html")
    public String viewPost(Model model) {

        // --- (데이터 생성 부분은 동일합니다) ---

        // 1. 가짜 작성자 데이터
        AuthorDto author = new AuthorDto("inspiration_hunter");

        // 2. 가짜 이미지 데이터 (Picsum)
        List<String> images = Arrays.asList(
                "https://picsum.photos/seed/detail1/800/600",
                "https://picsum.photos/seed/detail2/800/600"
        );

        // 3. 가짜 게시물 상세 데이터
        // 주소에 ID가 없으므로 무조건 ID가 1인 게시물을 보여줍니다.
        PostDetailDto post = new PostDetailDto(
                1L,
                "나만의 감성적인 작업 공간 꾸미기",
                "작업 공간은 단순히 일을 하는 곳이 아니라...",
                "인테리어",
                author,
                images,
                LocalDateTime.now(),
                true
        );

        // 4. 모델에 데이터 담기
        model.addAttribute("post", post);
        model.addAttribute("username", "모아나");

        // 5. view.html 파일로 이동
        return "post/view";
    }

    // --- DTO 클래스들은 그대로 두세요 ---
    static class PostDetailDto {
        private Long id;
        private String title;
        private String content;
        private String category;
        private AuthorDto author;
        private List<String> images;
        private LocalDateTime createdAt;
        private boolean isOwner;

        public PostDetailDto(Long id, String title, String content, String category,
                             AuthorDto author, List<String> images,
                             LocalDateTime createdAt, boolean isOwner) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.category = category;
            this.author = author;
            this.images = images;
            this.createdAt = createdAt;
            this.isOwner = isOwner;
        }

        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getContent() { return content; }
        public String getCategory() { return category; }
        public AuthorDto getAuthor() { return author; }
        public List<String> getImages() { return images; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public boolean getIsOwner() { return isOwner; }
    }

    static class AuthorDto {
        private String username;
        public AuthorDto(String username) { this.username = username; }
        public String getUsername() { return username; }
    }
}