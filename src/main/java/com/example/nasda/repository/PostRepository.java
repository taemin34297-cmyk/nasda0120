package com.example.nasda.repository;

import com.example.nasda.domain.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Integer> {

    // 단순 버전
    List<PostEntity> findAllByOrderByCreatedAtDesc();

    // ✅ 추천: N+1 방지용 fetch join
    @Query("""
        select p
        from PostEntity p
        join fetch p.user
        join fetch p.category
        order by p.createdAt desc
    """)
    List<PostEntity> findAllWithUserAndCategoryOrderByCreatedAtDesc();
}