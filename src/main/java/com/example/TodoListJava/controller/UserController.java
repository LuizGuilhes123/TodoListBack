package com.example.TodoListJava.controller;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import com.example.TodoListJava.dto.user.UserDTO;
import com.example.TodoListJava.dto.user.UserInsertDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Usuário")
@RestController
@RequestMapping(value = "/v1/usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @Operation(summary = "Busca um usuário por ID. ")
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id){
        var response = service.findById(id);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Cria um usuário. ")
    @PostMapping
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserInsertDTO usuarioDTO) {
        var newObj = service.create(usuarioDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newObj.getId()).toUri();
        return ResponseEntity.created(uri).body(newObj);
    }

    @Operation(summary = "Atualiza os dados do usuário. ")
    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@Valid @RequestBody UserDTO usuarioDTO, @PathVariable UUID id) {
        service.updateUser(usuarioDTO, id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Ativa a notificação do usuário. ")
    @PatchMapping(value = "ativarNotificacao/{id}")
    public ResponseEntity<UserDTO> activateNotification(@PathVariable UUID id) {
        var response  = service.activateNotification(id);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Usuário consegue fazer o envio de sua foto. ")
    @PatchMapping(value = "/{usuarioId}/img")
    public ResponseEntity<Void> uploadfile(@PathVariable UUID usuarioId, @RequestParam("imagem") MultipartFile imagem) throws IOException {
        service.uploadfile(usuarioId, imagem);
        return ResponseEntity.ok().build();
    }
}