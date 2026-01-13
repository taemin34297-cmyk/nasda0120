package com.example.nasda.repository;

import com.example.nasda.domain.PostImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PostImageRepository extends JpaRepository<PostImageEntity, Integer> {

    List<PostImageEntity> findByPost_PostIdOrderBySortOrderAsc(Integer postId);

    Optional<PostImageEntity> findFirstByPost_PostIdOrderBySortOrderAsc(Integer postId);

    @Transactional
    void deleteByPost_PostId(Integer postId);
}
