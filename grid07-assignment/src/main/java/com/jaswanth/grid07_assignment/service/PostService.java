package com.jaswanth.grid07_assignment.service;


import com.jaswanth.grid07_assignment.dtos.CreatePostRequest;
import com.jaswanth.grid07_assignment.entities.AuthorType;
import com.jaswanth.grid07_assignment.entities.Bot;
import com.jaswanth.grid07_assignment.entities.Post;
import com.jaswanth.grid07_assignment.entities.User;
import com.jaswanth.grid07_assignment.repositry.BotRepositry;
import com.jaswanth.grid07_assignment.repositry.PostRepositry;
import com.jaswanth.grid07_assignment.repositry.UserRepositry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.jaswanth.grid07_assignment.entities.AuthorType.BOT;

@Service
@RequiredArgsConstructor
public class PostService {

   private final RedisService redisService;
    private final PostRepositry postRepositry;
    private final UserRepositry userRepositry;
    private final BotRepositry botRepositry;

    @Transactional
    public Post createPost(CreatePostRequest createPostRequest){
         Post post = new Post();
        AuthorType authorType=createPostRequest.getAuthorType();
        if(authorType==null){
            throw new IllegalArgumentException("authorType must be  provided");
        }
        post.setContent(createPostRequest.getContent());
        if(authorType==BOT){
            if (createPostRequest.getBotId() == null)
                throw new IllegalArgumentException("botId must be provided when authorType is BOT");
            Bot bot=botRepositry.findById(createPostRequest.getBotId()).orElseThrow(()->new RuntimeException("bot id not found"));
            post.setBot(bot);
        }
        else{
            if(createPostRequest.getUserId() == null){
                throw new IllegalArgumentException("userId must be provided when authorType is USER");
            }
            User user=userRepositry.findById(createPostRequest.getUserId()).orElseThrow(()->new RuntimeException("user id not found"));
            post.setUser(user);

        }
        post.setAuthorType(authorType);
        postRepositry.save(post);
        return post;

    }
   @Transactional
    public Post likePost(Long postId){

        Post post=postRepositry.findById(postId).orElseThrow(()->new RuntimeException("post not found"));
        redisService.incrementViralityScore(postId,20);
        post.setLikeCount(post.getLikeCount()+1);
        postRepositry.save(post);
        return post;
    }
}
