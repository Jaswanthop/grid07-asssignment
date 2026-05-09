package com.jaswanth.grid07_assignment.controllers;


import com.jaswanth.grid07_assignment.dtos.ApiResponse;
import com.jaswanth.grid07_assignment.dtos.CreateCommentRequest;
import com.jaswanth.grid07_assignment.dtos.CreatePostRequest;
import com.jaswanth.grid07_assignment.entities.Comment;
import com.jaswanth.grid07_assignment.entities.Post;
import com.jaswanth.grid07_assignment.service.CommentService;
import com.jaswanth.grid07_assignment.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    PostService postService;
    CommentService commentService;

    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;

    }


   @PostMapping
    public ResponseEntity<ApiResponse<Post>> createPost(@RequestBody CreatePostRequest createPostRequest){

        Post post=postService.createPost(createPostRequest);

       return ResponseEntity
               .status(HttpStatus.CREATED)
               .body(new ApiResponse<>("Post created successfully",201,post));
    }
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<Comment>> createComment(@RequestBody CreateCommentRequest createCommentRequest, @PathVariable("postId") Long postId){
        Comment comment=commentService.addComment(createCommentRequest,postId);


        return  ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Comment created successfully",201,comment));

    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Post>> likePost(@PathVariable("postId") Long postId){

        Post post=postService.likePost(postId);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("liked the post",201,post));

    }


}
