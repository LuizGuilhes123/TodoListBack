package com.example.TodoListJava.dto;

import lombok.Builder;

@Builder
public record TokenResponseDTO(String accessToken, String refreshToken) {

}