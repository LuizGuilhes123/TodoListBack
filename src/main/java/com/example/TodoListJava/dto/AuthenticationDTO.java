package com.example.TodoListJava.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationDTO(@NotBlank(message = "Campo obrigatório.") String email, @NotBlank(message = "Campo obrigatório.") String senha) {

}