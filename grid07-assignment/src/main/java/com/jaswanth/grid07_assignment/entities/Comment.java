package com.jaswanth.grid07_assignment.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "author_type")
    private AuthorType authorType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id")
    private Post post;


    @ManyToOne(fetch = FetchType.LAZY,optional = true)
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne(fetch = FetchType.LAZY,optional = true)
    @JoinColumn(name = "bot_id")
    private Bot bot;


    @Column(nullable = false)
    private String content;

   @Column(nullable = false,name="depth_level")
    private int depthLevel;


   @CreationTimestamp
   @Column(updatable = false,name="created_at")
   private LocalDateTime createdAt;


   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name="parent_comment_id")
   private Comment parentComment;
}
