package com.example.TodoListJava.dto.position;

import com.example.TodoListJava.entity.PositionEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PositionDTO {

    private Long id;

    private String authority;

    public PositionDTO(PositionEntity entity) {
        id = entity.getId();
        authority = entity.getAuthority();
    }
}
