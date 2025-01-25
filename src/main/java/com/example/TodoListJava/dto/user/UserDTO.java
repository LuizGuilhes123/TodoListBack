package com.example.TodoListJava.dto.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import com.example.TodoListJava.dto.position.PositionDTO;
import com.example.TodoListJava.dto.task.TaskDTO;
import com.example.TodoListJava.entity.UserEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;

    @Size(max = 124, message = "máximo de caracteres 124")
    @NotBlank(message = "campo obrigatório ")
    private String name;

    @Email(message = "Entre com um Email válido.", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$")
    @NotBlank(message = "campo obrigatório ")
    private String email;

    private List<PositionDTO> cargos = new ArrayList<>();

    private List<TaskDTO> tarefas = new ArrayList<>();

    private Boolean notification;

    private String imgUrl;

    public UserDTO(UserEntity entity) {
        id = entity.getId();
        name = entity.getName();
        email = entity.getEmail();
        entity.getCargos().stream()
                .forEach(car -> this.cargos.add(new PositionDTO(car)));
        entity.getTarefas().stream()
                .forEach(tar -> this.tarefas.add(new TaskDTO(tar)));
        notification = entity.getNotification();
        imgUrl = entity.getImgUrl();
    }

}