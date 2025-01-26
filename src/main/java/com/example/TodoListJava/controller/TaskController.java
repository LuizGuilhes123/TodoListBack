package com.example.TodoListJava.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.TodoListJava.dto.task.MoveTaskRecordDTO;
import com.example.TodoListJava.dto.task.TaskDTO;
import com.example.TodoListJava.dto.task.TaskRecordDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Tasks")
@RestController
@RequestMapping(value = "/v1/tarefas")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @Operation(summary = "Busca uma tarefa por ID. ")
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        var response = service.findById(id);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Lista todas as tarefas do usuário usando o ID. ")
    @GetMapping(value = "/minhasTasks/{id}")
    public ResponseEntity<List<TaskDTO>> listAll(@PathVariable UUID id){
        var response = service.listAll(id);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Cria uma tarefa passando o ID do usuário. ")
    @PostMapping(value = "/{id}")
    public ResponseEntity<TaskDTO> create(@PathVariable UUID id,@Valid @RequestBody TaskRecordDTO tarefaRecord){
        var newObj = service.create(tarefaRecord, id);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newObj.getId())
                .toUri();
        return ResponseEntity.created(uri).body(newObj);
    }

    @Operation(summary = "Atualiza uma tarefa. ")
    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@Valid @RequestBody TaskRecordDTO tarefa, @PathVariable Long id){
        service.update(tarefa, id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Deleta uma tarefa. ")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Habilita mover a tarefa dentro da lista trocando sua posição. ")
    @PostMapping(value = "/move/{id}")
    public ResponseEntity<Void> moveTask(@RequestBody MoveTaskRecordDTO moveDTO, @PathVariable UUID id) {
        service.moveTask(id, moveDTO.sourceIndex(), moveDTO.destinationIndex());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Atualiza parcialmente uma Task DTO.")
    @PatchMapping(value = "/{id}")
    public ResponseEntity<TaskDTO> patchUpdate(@RequestBody Map<String, Object> fields, @PathVariable Long id) {
        var response = service.patchUpdate(fields, id);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Defini uma tarefa como favorita.")
    @PatchMapping(value = "/DefinirTaskFavorita/{id}")
    public ResponseEntity<TaskDTO> favorite(@PathVariable Long id) {
        var response = service.activateFavorite(id);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Lista todas as tarefas favoritas do usuário.")
    @GetMapping(value = "/tarefasFavoritas/{id}")
    public ResponseEntity<List<TaskDTO>> listFavoriteTesks(@PathVariable UUID id) {
        var response = service.getFavorites(id);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Lista todas as tarefas criadas no mesmo dia. ")
    @GetMapping(value = "/tarefasHoje/{id}")
    public ResponseEntity<List<TaskDTO>> listTodayTasks(@PathVariable UUID id) {
        var response = service.getTodayTasks(id);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Lista todas as tarefas criadas na semana. ")
    @GetMapping(value = "/tarefasSemana/{id}")
    public ResponseEntity<List<TaskDTO>> listWeeklyTasks(@PathVariable UUID id) {
        var response = service.getWeeklyTasks(id);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Lista todas as tarefas criadas no mês. ")
    @GetMapping(value = "/tarefasMes/{id}")
    public ResponseEntity<List<TaskDTO>> listMonthlyTasks(@PathVariable UUID id) {
        var response = service.getMonthlyTasks(id);
        return ResponseEntity.ok().body(response);
    }
}