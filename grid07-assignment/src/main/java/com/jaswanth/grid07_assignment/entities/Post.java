package com.jaswanth.grid07_assignment.entities;


import jakarta.persistence.*;
import lombok.*;

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
    private Long authorId;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,name="author_type")
    private AuthorType authorType;

    @Column(updatable = false,name="created_at")
    private LocalDateTime createdAt;

    @Column(name="like_count",nullable = false)
    private long likeCount=0;

}
