package com.jaswanth.grid07_assignment.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false,unique = true)
   private String username;

   @Column(nullable = false,name="is_premimum")
   private boolean isPremium=false;
}
