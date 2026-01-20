package com.example.nasda.controller;

import com.example.nasda.domain.CategoryEntity;
import com.example.nasda.domain.PostEntity;
import com.example.nasda.dto.post.PostCreateRequestDto;
import com.example.nasda.dto.post.PostViewDto;
import com.example.nasda.service.AuthUserService;
import com.example.nasda.service.CategoryService;
import com.example.nasda.service.CommentService;
import com.example.nasda.service.PostImageService;
import com.example.nasda.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CategoryService categoryService;
    private final CommentService commentService;
    private final PostImageService postImageService;
    private final AuthUserService authUserService;

    @GetMapping("/posts")
    public String postsRedirect() {
        return "redirect:/";
    }

    @GetMapping("/posts/create")
    public String createForm(Model model) {
        model.addAttribute("postCreateRequestDto", new PostCreateRequestDto("", "", ""));
        model.addAttribute("categories", categoryService.findAll());

        String nickname = authUserService.getCurrentNicknameOrNull();
        model.addAttribute("username", nickname == null ? "게스트" : nickname);

        return "post/create";
    }

    @GetMapping("/posts/{postId}")
    public String viewPost(
            @PathVariable("postId") String postIdStr,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model
    ) {
        try {
            // /posts/create 와 충돌 방지
            if ("create".equals(postIdStr)) return "redirect:/posts/create";

            Integer postId = Integer.parseInt(postIdStr);
            PostEntity entity = postService.get(postId);

            // 1. 로그인 유저 및 본인 확인 로직 (작성자가 null일 경우 대비)
            Integer currentUserId = authUserService.getCurrentUserIdOrNull();
            boolean isOwner = currentUserId != null
                    && entity.getUser() != null
                    && currentUserId.equals(entity.getUser().getUserId());

            // 2. 이미지 정보 가져오기
            List<String> imageUrls = postImageService.getImageUrls(postId);
            List<PostViewDto.ImageDto> imageItems = postService.getImageItems(postId);

            // ✅ 3. 작성자(Author) 정보 안전하게 생성 (Null 처리 핵심)
            PostViewDto.AuthorDto authorDto;
            if (entity.getUser() != null) {
                authorDto = new PostViewDto.AuthorDto(entity.getUser().getNickname());
            } else {
                // 작성자가 탈퇴하여 null인 경우 처리
                authorDto = new PostViewDto.AuthorDto("(알 수 없음)");
            }

            // 4. 카테고리 이름 안전하게 처리
            String categoryName = (entity.getCategory() != null)
                    ? entity.getCategory().getCategoryName()
                    : "미분류";

            // 5. 화면에 전달할 DTO 조립
            PostViewDto post = new PostViewDto(
                    entity.getPostId(),
                    entity.getTitle(),
                    entity.getDescription() != null ? entity.getDescription() : "",
                    categoryName,
                    authorDto,
                    imageUrls,
                    imageItems,
                    entity.getCreatedAt(),
                    isOwner
            );

            // 6. 댓글 페이지 처리
            var commentsPage = commentService.getCommentsPage(postId, page, size, currentUserId);

            // 7. 모델에 데이터 담기
            model.addAttribute("post", post);
            model.addAttribute("comments", commentsPage.getContent());
            model.addAttribute("commentsPage", commentsPage);

            String nickname = authUserService.getCurrentNicknameOrNull();
            model.addAttribute("username", nickname == null ? "게스트" : nickname);

            return "post/view";
        } catch (NumberFormatException e) {
            return "redirect:/";
        }
    }
    @PostMapping("/posts")
    public String createPost(
            @RequestParam String title,
            @RequestParam String category,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<MultipartFile> images
    ) {
        Integer userId = authUserService.getCurrentUserIdOrNull();
        if (userId == null) return "redirect:/user/login";

        CategoryEntity categoryEntity = categoryService.getByNameOrThrow(category);
        PostEntity post = postService.create(userId, categoryEntity.getCategoryId(), title, description);

        if (images != null && !images.isEmpty()) {
            postImageService.addImages(post, images);
        }

        return "redirect:/posts/" + post.getPostId();
    }

    @GetMapping("/posts/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        PostEntity entity = postService.get(id);

        model.addAttribute("postId", entity.getPostId());
        model.addAttribute("title", entity.getTitle());
        model.addAttribute("description", entity.getDescription());
        model.addAttribute("category", entity.getCategory().getCategoryName());
        model.addAttribute("images", List.of());
        model.addAttribute("categories", categoryService.findAll());

        String nickname = authUserService.getCurrentNicknameOrNull();
        model.addAttribute("username", nickname == null ? "게스트" : nickname);

        return "post/edit";
    }

    @PostMapping("/posts/{id}/edit")
    public String editPost(
            @PathVariable Integer id,
            @RequestParam String title,
            @RequestParam String category,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<MultipartFile> newImages
    ) {
        Integer userId = authUserService.getCurrentUserIdOrNull();
        if (userId == null) return "redirect:/user/login";

        CategoryEntity categoryEntity = categoryService.getByNameOrThrow(category);

        postService.update(id, userId, categoryEntity.getCategoryId(), title, description);

        PostEntity post = postService.get(id);
        postImageService.replaceImages(id, post, newImages);

        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Integer id) {
        Integer userId = authUserService.getCurrentUserIdOrNull();
        if (userId == null) return "redirect:/user/login";

        postService.delete(id, userId);
        return "redirect:/posts/my";
    }

    @GetMapping("/posts/my")
    public String myPosts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model
    ) {
        Integer userId = authUserService.getCurrentUserIdOrNull();
        if (userId == null) return "redirect:/user/login";

        Page<PostEntity> paging = postService.findByUserId(userId, page);
        model.addAttribute("paging", paging);

        String nickname = authUserService.getCurrentNicknameOrNull();
        model.addAttribute("username", nickname == null ? "게스트" : nickname);

        return "post/my-list";
    }
}
