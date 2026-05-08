package com.jaswanth.grid07_assignment.dtos;

import com.jaswanth.grid07_assignment.entities.AuthorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class CreateCommentRequest {
    private AuthorType authorType;
    private Long userId;
    private Long postId;
    private Long parentCommentId;
    private String content;
    private Long botId;

}
