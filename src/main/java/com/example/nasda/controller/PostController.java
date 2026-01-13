package com.example.nasda.controller;

import com.example.nasda.domain.CategoryEntity;
import com.example.nasda.domain.PostEntity;
import com.example.nasda.domain.UserEntity;
import com.example.nasda.domain.UserRepository;
import com.example.nasda.dto.post.PostCreateRequestDto;
import com.example.nasda.dto.post.PostViewDto;
import com.example.nasda.repository.CategoryRepository;
import com.example.nasda.service.CommentService;
import com.example.nasda.service.PostImageService;
import com.example.nasda.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CategoryRepository categoryRepository;
    private final CommentService commentService;
    private final PostImageService postImageService;

    // ✅ PrincipalDetailsService가 참조하는 레포 (loginId -> UserEntity 조회)
    private final UserRepository userRepository;

    // =========================
    // 로그인 사용자 정보 (SecurityUtil 대체)
    // =========================
    private String getLoginIdOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;

        Object principal = auth.getPrincipal();
        if (principal == null) return null;
        if ("anonymousUser".equals(principal)) return null;

        String loginId = auth.getName(); // ✅ PrincipalDetailsService에서 username=loginId
        if (loginId == null || loginId.isBlank()) return null;

        return loginId;
    }

    private Integer getCurrentUserIdOrNull() {
        String loginId = getLoginIdOrNull();
        if (loginId == null) return null;

        return userRepository.findByLoginId(loginId)
                .map(UserEntity::getUserId)
                .orElse(null);
    }

    private String getCurrentNicknameOrNull() {
        String loginId = getLoginIdOrNull();
        if (loginId == null) return null;

        return userRepository.findByLoginId(loginId)
                .map(UserEntity::getNickname)
                .orElse(null);
    }

    // =========================
    // 게시글 상세
    // =========================
    @GetMapping("/post/view.html")
    public String viewPost(
            @RequestParam("id") Integer postId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model
    ) {
        PostEntity entity = postService.get(postId);

        List<String> imageUrls = postImageService.getImageUrls(postId);

        Integer currentUserId = getCurrentUserIdOrNull();

        boolean isOwner = currentUserId != null
                && entity.getUser() != null
                && currentUserId.equals(entity.getUser().getUserId());

        PostViewDto post = new PostViewDto(
                entity.getPostId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCategory().getCategoryName(),
                new PostViewDto.AuthorDto(entity.getUser().getNickname()),
                imageUrls,
                entity.getCreatedAt(),
                isOwner
        );

        var commentsPage = commentService.getCommentsPage(postId, page, size, currentUserId);

        model.addAttribute("post", post);
        model.addAttribute("comments", commentsPage.getContent());
        model.addAttribute("commentsPage", commentsPage);

        // ✅ 헤더에서 쓰는 username: 로그인 닉네임, 없으면 게스트
        String nickname = getCurrentNicknameOrNull();
        model.addAttribute("username", nickname == null ? "게스트" : nickname);

        return "post/view";
    }

    // =========================
    // 홈 리스트
    // =========================
    @GetMapping("/posts")
    public String list(Model model) {
        model.addAttribute("posts", postService.getHomePosts());
        return "index";
    }

    // 구 URL 호환
    @GetMapping("/posts/{postId}")
    public String viewCompat(@PathVariable Integer postId) {
        return "redirect:/post/view.html?id=" + postId;
    }

    // =========================
    // 글 작성
    // =========================
    @GetMapping("/posts/create")
    public String createForm(Model model) {
        model.addAttribute("postCreateRequestDto", new PostCreateRequestDto("", "", ""));
        model.addAttribute("categories", categoryRepository.findAll());
        return "post/create";
    }

    @PostMapping("/posts")
    public String createPost(
            @RequestParam String title,
            @RequestParam String category,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<org.springframework.web.multipart.MultipartFile> images
    ) {
        Integer userId = getCurrentUserIdOrNull();
        if (userId == null) return "redirect:/user/login";

        CategoryEntity categoryEntity = categoryRepository.findByCategoryName(category)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리: " + category));

        PostEntity post = postService.create(
                userId,
                categoryEntity.getCategoryId(),
                title,
                description
        );

        postImageService.addImages(post, images);

        return "redirect:/posts/" + post.getPostId();
    }

    // =========================
    // 글 수정
    // =========================
    @GetMapping("/posts/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        PostEntity entity = postService.get(id);

        model.addAttribute("postId", entity.getPostId());
        model.addAttribute("title", entity.getTitle());
        model.addAttribute("description", entity.getDescription());
        model.addAttribute("category", entity.getCategory().getCategoryName());
        model.addAttribute("images", List.of()); // 기존 유지
        model.addAttribute("categories", categoryRepository.findAll());

        return "post/edit";
    }

    @PostMapping("/posts/{id}/edit")
    public String editPost(
            @PathVariable Integer id,
            @RequestParam String title,
            @RequestParam String category,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<org.springframework.web.multipart.MultipartFile> newImages
    ) {
        Integer userId = getCurrentUserIdOrNull();
        if (userId == null) return "redirect:/user/login";

        CategoryEntity categoryEntity = categoryRepository.findByCategoryName(category)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리: " + category));

        postService.update(id, userId, categoryEntity.getCategoryId(), title, description);

        PostEntity post = postService.get(id);
        postImageService.replaceImages(id, post, newImages);

        return "redirect:/post/view.html?id=" + id;
    }

    // =========================
    // 글 삭제
    // =========================
    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Integer id) {
        Integer userId = getCurrentUserIdOrNull();
        if (userId == null) return "redirect:/user/login";

        postService.delete(id, userId);
        return "redirect:/";
    }
}
