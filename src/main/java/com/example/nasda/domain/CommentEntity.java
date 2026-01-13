package com.example.nasda.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments",
        indexes = {
                @Index(name = "idx_comments_post_created", columnList = "post_id, created_at")
        })
@Getter
@NoArgsConstructor
public class CommentEntity {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static CommentEntity create(PostEntity post, Integer userId, String content) {
        CommentEntity c = new CommentEntity();
        c.post = post;
        c.userId = userId;
        c.content = content;
        return c;
    }

    public void edit(String content) {
        this.content = content;
    }
}
