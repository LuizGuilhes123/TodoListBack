package com.example.TodoListJava.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInsertDTO extends UserDTO {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "campo obrigatório ")
    private String senha;
}