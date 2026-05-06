package com.jaswanth.grid07_assignment.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,name="author_type")
    private AuthorType authorType;

    @CreationTimestamp
    @Column(updatable = false,name="created_at")
    private LocalDateTime createdAt;

    @Column(name="like_count",nullable = false)
    private long likeCount=0;

    @ManyToOne(fetch = FetchType.LAZY,optional = true)
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne(fetch = FetchType.LAZY,optional = true)
    @JoinColumn(name = "bot_id")
    private Bot bot;
}
