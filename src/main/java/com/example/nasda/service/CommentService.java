package com.example.nasda.service;

import com.example.nasda.domain.CommentEntity;
import com.example.nasda.domain.PostEntity;
import com.example.nasda.domain.UserRepository;
import com.example.nasda.dto.comment.CommentViewDto;
import com.example.nasda.repository.CommentRepository;
import com.example.nasda.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository; // ✅ 1. 닉네임 조회를 위해 추가

    public Page<CommentViewDto> getCommentsPage(Integer postId, int page, int size, Integer currentUserId) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, size);

        return commentRepository
                .findByPost_PostIdOrderByCreatedAtDesc(postId, PageRequest.of(safePage, safeSize))
                .map(e -> {
                    Integer authorId = e.getUserId();
                    String nickname = "(알 수 없음)"; // 기본값

                    // ✅ 2. 작성자가 존재하면 실제 닉네임을 DB에서 찾아옵니다.
                    if (authorId != null) {
                        nickname = userRepository.findById(authorId)
                                .map(user -> user.getNickname())
                                .orElse("(알 수 없음)");
                    }

                    // ✅ 3. 500 에러 방지 (null 체크)
                    boolean canEdit = authorId != null && authorId.equals(currentUserId);

                    return new CommentViewDto(
                            e.getCommentId(),
                            e.getContent(),
                            nickname, // 👈 이제 "사용자5"가 아니라 "진짜 닉네임"이 들어갑니다.
                            e.getCreatedAt(),
                            canEdit
                    );
                });
    }

    @Transactional
    public Integer createComment(Integer postId, Integer userId, String content) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글: " + postId));

        String trimmed = content == null ? "" : content.trim();
        if (trimmed.isEmpty()) throw new IllegalArgumentException("댓글 내용이 비어있습니다.");
        if (trimmed.length() > 500) throw new IllegalArgumentException("댓글은 최대 500자까지 가능합니다.");

        // ✅ 팀 프로젝트용 팩토리 메서드 호출 (기존 로직 유지)
        CommentEntity c = CommentEntity.create(post, userId, trimmed);
        CommentEntity saved = commentRepository.save(c);
        return saved.getCommentId();
    }

    public int getLastPageIndex(Integer postId, int size) {
        int safeSize = Math.max(1, size);
        long total = commentRepository.countByPost_PostId(postId);
        if (total <= 0) return 0;
        return (int) ((total - 1) / safeSize);
    }

    @Transactional
    public Integer deleteComment(Integer commentId, Integer currentUserId) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다. id=" + commentId));

        // ✅ 수정 3: 권한 체크 시 null 안전성 확보
        if (comment.getUserId() == null || !comment.getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException("본인 댓글만 삭제할 수 있습니다.");
        }

        Integer postId = comment.getPost().getPostId();
        commentRepository.delete(comment);
        return postId;
    }

    @Transactional
    public Integer editComment(Integer commentId, Integer currentUserId, String newContent) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다. id=" + commentId));

        if (comment.getUserId() == null || !comment.getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException("본인 댓글만 수정할 수 있습니다.");
        }

        String trimmed = newContent == null ? "" : newContent.trim();
        if (trimmed.isEmpty()) throw new IllegalArgumentException("댓글 내용이 비어있습니다.");
        if (trimmed.length() > 500) throw new IllegalArgumentException("댓글은 최대 500자까지 가능합니다.");

        comment.edit(trimmed);
        return comment.getPost().getPostId();
    }

    @Transactional(readOnly = true)
    public Page<CommentEntity> findByUserId(Integer userId, Pageable pageable) {
        return commentRepository.findByUserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public int getPageNumberByCommentId(Integer postId, Integer commentId, int pageSize) {
        List<CommentEntity> allComments = commentRepository.findByPost_PostIdOrderByCreatedAtDesc(postId);
        int index = 0;
        for (int i = 0; i < allComments.size(); i++) {
            if (allComments.get(i).getCommentId().equals(commentId)) {
                index = i;
                break;
            }
        }
        return index / pageSize;
    }
}