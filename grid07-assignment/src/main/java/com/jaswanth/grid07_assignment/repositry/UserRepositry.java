package com.jaswanth.grid07_assignment.repositry;

import com.jaswanth.grid07_assignment.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepositry extends JpaRepository<User,Long> {
}
