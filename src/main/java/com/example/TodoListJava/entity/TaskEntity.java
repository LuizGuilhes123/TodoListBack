package com.example.TodoListJava.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@Entity
@Table(
        name = "tb_task",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"})
)
public class TaskEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String name;

    private BigDecimal cost;

    private LocalDate dueDate;

    private Integer position;

    private Boolean favorite;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
