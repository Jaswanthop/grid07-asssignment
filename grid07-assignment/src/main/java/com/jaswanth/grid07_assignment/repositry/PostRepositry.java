package com.jaswanth.grid07_assignment.repositry;

import com.jaswanth.grid07_assignment.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepositry extends JpaRepository<Post,Long> {


}
