package com.example.TodoListJava.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode(of = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_user")
public class UserEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Column(unique = true, nullable = true)
    private String email;

    @JsonIgnore
    private String password;

    private Boolean notification;

    @Column(columnDefinition = "TEXT", name = "IMG_URL")
    private String imgUrl;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "tb_user_cargo", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "cargo_id"))
    private Set<PositionEntity> cargos = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<TaskEntity> tarefas = new ArrayList<>();
    public boolean hasPosition(String positionName) {
        return cargos.stream()
                .anyMatch(position -> position.getAuthority().equals(positionName));
    }
}
