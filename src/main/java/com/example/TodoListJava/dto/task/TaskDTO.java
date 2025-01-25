package com.example.TodoListJava.dto.task;

import com.example.TodoListJava.entity.TaskEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private BigDecimal cost;

    private LocalDate dueDate;

    private int position;

    private Boolean favorite;

    public TaskDTO(TaskEntity entity) {
        id = entity.getId();
        name = entity.getName();
        cost = entity.getCost();
        dueDate = entity.getDueDate();
        position = entity.getPosition();
        favorite = entity.getFavorite();
    }

}
