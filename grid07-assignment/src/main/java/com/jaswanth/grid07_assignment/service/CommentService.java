package com.jaswanth.grid07_assignment.service;

import com.jaswanth.grid07_assignment.dtos.CreateCommentRequest;
import com.jaswanth.grid07_assignment.entities.*;
import com.jaswanth.grid07_assignment.repositry.BotRepositry;
import com.jaswanth.grid07_assignment.repositry.CommentRepositry;
import com.jaswanth.grid07_assignment.repositry.PostRepositry;
import com.jaswanth.grid07_assignment.repositry.UserRepositry;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class CommentService {

    private UserRepositry userRepositry;
    private PostRepositry postRepositry;
    private CommentRepositry commentRepositry;
    private BotRepositry botRepositry;

   @Transactional
    public Comment addComment(CreateCommentRequest createCommentRequest,Long postId){
        Comment comment = new Comment();
       if(postId == null){
           throw new IllegalArgumentException("postId must be provided to add comment");

       }

        Post post=postRepositry.findById(postId).orElseThrow(()->new RuntimeException("post not found"));
        //post
        comment.setPost(post);
        AuthorType authorType=createCommentRequest.getAuthorType();
        if(authorType==null){
            throw new IllegalArgumentException("authorType must be  provided");
        }
        if(authorType==AuthorType.USER){
            if(createCommentRequest.getUserId()==null){
                throw new IllegalArgumentException("userId must be  provided when authorType is USER");
            }
            User user=userRepositry.findById(createCommentRequest.getUserId()).orElseThrow(()->new RuntimeException("user not found"));
            comment.setUser(user);

        }
        else{
            if(createCommentRequest.getBotId()==null){
                throw new IllegalArgumentException("botId must be  provided when authorType is Bot");

            }
            Bot bot =botRepositry.findById(createCommentRequest.getBotId()).orElseThrow(()->new RuntimeException("bot not found"));
            comment.setBot(bot);
        }
        //author type and content
        comment.setAuthorType(authorType);
        comment.setContent(createCommentRequest.getContent());

        //depth
        Long parentId=createCommentRequest.getParentCommentId();
        if(parentId!=null){
            Comment parent=commentRepositry.findById(parentId).orElseThrow(()->new RuntimeException("parent comment not found"));
            //parent
            comment.setParentComment(parent);
            comment.setDepthLevel(parent.getDepthLevel()+1);
        }else{
            comment.setDepthLevel(0);
        }
        commentRepositry.save(comment);
        return comment;
    }
}
