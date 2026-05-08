package com.jaswanth.grid07_assignment.dtos;


import com.jaswanth.grid07_assignment.entities.AuthorType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreatePostRequest {

    private AuthorType authorType;
    private String content;
    private Long userId;
    private Long botId;
}
