package com.jaswanth.grid07_assignment.repositry;

import com.jaswanth.grid07_assignment.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepositry extends JpaRepository<Comment,Long> {

    public int  countByPostIdAndAuthorType(long postId,String authorType);
}
