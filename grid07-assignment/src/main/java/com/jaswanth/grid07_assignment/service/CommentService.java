package com.jaswanth.grid07_assignment.service;

import com.jaswanth.grid07_assignment.dtos.CreateCommentRequest;
import com.jaswanth.grid07_assignment.entities.*;
import com.jaswanth.grid07_assignment.repositry.BotRepositry;
import com.jaswanth.grid07_assignment.repositry.CommentRepositry;
import com.jaswanth.grid07_assignment.repositry.PostRepositry;
import com.jaswanth.grid07_assignment.repositry.UserRepositry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CommentService {
   Logger log = LoggerFactory.getLogger(CommentService.class);
    private final CommentRepositry commentRepositry;
    private final UserRepositry userRepositry;
    private final PostRepositry postRepositry;
    private final BotRepositry botRepositry;
    private final RedisService redisService;

    @Transactional
    public Comment addComment(CreateCommentRequest createCommentRequest, Long postId){
        Comment comment = new Comment();
        if(postId == null){
           throw new IllegalArgumentException("postId must be provided to add comment");

        }

        Post post=postRepositry.findById(postId).orElseThrow(()->new RuntimeException("post not found"));
        comment.setPost(post);
        AuthorType authorType=createCommentRequest.getAuthorType();
        if(authorType==null){
            throw new IllegalArgumentException("authorType must be  provided");
        }

        Comment parent = null;
        Long parentId = createCommentRequest.getParentCommentId();
        if(parentId != null){
            parent=commentRepositry.findById(parentId).orElseThrow(()->new RuntimeException("parent comment not found"));
            int newDepth = parent.getDepthLevel() + 1;
            //vertical cap
            if(newDepth > 20) {
                throwTooManyRequests("comment depth cannot be greater than 20");
            }
            comment.setParentComment(parent);
            comment.setDepthLevel(newDepth);
        }else{
            comment.setDepthLevel(0);
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
            //horizontal cap
            if(redisService.isHorizontalCapReached(postId)) {
                throwTooManyRequests("post cannot have more than 100 bot replies");
            }

            Long humanId = getHumanIdForBotInteraction(post, parent);
            if(humanId != null && redisService.isBotOnCooldown(createCommentRequest.getBotId(), humanId)){
                redisService.decrementHorizontalCap(postId);
                throwTooManyRequests("bot cannot interact with this human more than once per 10 minutes");
            }
            comment.setBot(bot);
        }

        comment.setAuthorType(authorType);
        comment.setContent(createCommentRequest.getContent());

        try {
            commentRepositry.save(comment);
        } catch (RuntimeException ex) {
            if(authorType == AuthorType.BOT) {
                redisService.decrementHorizontalCap(postId);
            }
            throw ex;
        }

        if(authorType == AuthorType.BOT) {
            redisService.incrementViralityScore(postId, 1);
        } else {
            redisService.incrementViralityScore(postId, 50);
        }


        //nofitication
        if (authorType == AuthorType.BOT && post.getAuthorType() == AuthorType.USER) {
            Long postAuthorId = post.getUser().getId();
            String botName = comment.getBot().getName();
            String message = "Bot " + botName + " replied to your post";

            if (redisService.isNotificationOnCooldown(postAuthorId)) {
                redisService.pushPendingNotification(postAuthorId, message);
            } else {
                log.info("Push Notification Sent to User {}", postAuthorId);
            }
        }
        return comment;
    }

    private Long getHumanIdForBotInteraction(Post post, Comment parent) {
        if(parent != null && parent.getAuthorType() == AuthorType.USER && parent.getUser() != null) {
            return parent.getUser().getId();
        }
        if(post.getAuthorType() == AuthorType.USER && post.getUser() != null) {
            return post.getUser().getId();
        }
        return null;
    }

    private void throwTooManyRequests(String message) {
        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, message);
    }
}
