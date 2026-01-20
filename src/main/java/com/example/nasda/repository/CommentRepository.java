package com.example.nasda.repository;

import com.example.nasda.domain.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {

    List<CommentEntity> findByPost_PostIdOrderByCreatedAtDesc(Integer postId);

    Page<CommentEntity> findByPost_PostIdOrderByCreatedAtDesc(Integer postId, Pageable pageable);

    // ✅ PostId 기준 카운트
    long countByPost_PostId(Integer postId);

    // ✅ UserId 기준 카운트 (이름을 엔티티 구조인 User_UserId로 변경)
    long countByUser_UserId(Integer userId);

    // ✅ 유저별 댓글 찾기
    Page<CommentEntity> findByUser_UserId(Integer userId, Pageable pageable);

    @Modifying
    @Query("UPDATE CommentEntity c SET c.user = null WHERE c.user.userId = :userId")
    void setAuthorNull(@Param("userId") Integer userId);
}