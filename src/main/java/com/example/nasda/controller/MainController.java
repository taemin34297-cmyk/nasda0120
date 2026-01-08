package com.example.nasda.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    @GetMapping("/")
    public String index(Model model) {
        // 1. 가짜 게시물 데이터 생성
        List<PostDto> posts = new ArrayList<>();

        // 이미지 주소를 리스트가 아닌 단일 문자열로 전달합니다.
        posts.add(new PostDto(1L, "첫 번째 영감", "멋진 디자인입니다.", "디자인", "user1", "https://via.placeholder.com/300"));
        posts.add(new PostDto(2L, "맛있는 요리", "건강한 레시피 공유해요.", "음식", "user2", "https://via.placeholder.com/300"));
        posts.add(new PostDto(3L, "맛있는 요리", "건강한 레시피 공유해요.", "운동", "user3", "https://via.placeholder.com/300"));
        posts.add(new PostDto(4L, "맛있는 요리", "건강한 레시피 공유해요.", "취미", "user4", "https://via.placeholder.com/300"));
        posts.add(new PostDto(5L, "맛있는 요리", "건강한 레시피 공유해요.", "반려동물", "user1", "https://via.placeholder.com/300"));
        posts.add(new PostDto(6L, "맛있는 요리", "건강한 레시피 공유해요.", "가족", "user2", "https://via.placeholder.com/300"));
        posts.add(new PostDto(7L, "맛있는 요리", "건강한 레시피 공유해요.", "꽃", "user3", "https://via.placeholder.com/300"));
        posts.add(new PostDto(8L, "맛있는 요리", "건강한 레시피 공유해요.", "자연", "user4", "https://via.placeholder.com/300"));
        posts.add(new PostDto(9L, "맛있는 요리", "건강한 레시피 공유해요.", "일기", "user5", "https://via.placeholder.com/300"));
        posts.add(new PostDto(10L, "맛있는 요리", "건강한 레시피 공유해요.", "예술", "user6", "https://via.placeholder.com/300"));

        // 2. HTML로 데이터 전달
        model.addAttribute("posts", posts);
        model.addAttribute("username", "모아나");
        model.addAttribute("category", "전체");

        return "index"; // templates/index.html을 찾아감
    }

    // DTO 클래스
    static class PostDto {
        private Long id;
        private String title;
        private String description;
        private String category;
        private Author author;
        private String imageUrl; // [수정] List<String> images -> String imageUrl (HTML 변수명과 일치시킴)

        public PostDto(Long id, String title, String description, String category, String username, String imageUrl) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.category = category;
            this.author = new Author(username);
            this.imageUrl = imageUrl;
        }

        // [중요] Thymeleaf가 데이터를 읽으려면 반드시 Getter가 필요합니다!
        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getCategory() { return category; }
        public Author getAuthor() { return author; }
        public String getImageUrl() { return imageUrl; } // 이게 없어서 에러가 났던 겁니다!
    }

    static class Author {
        private String username;

        public Author(String username) {
            this.username = username;
        }

        // Getter 추가
        public String getUsername() { return username; }
    }
}