package com.jaswanth.grid07_assignment.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ApiResponse<T>{
    private  String message;
    private  int  status;
    private  T data;
}
