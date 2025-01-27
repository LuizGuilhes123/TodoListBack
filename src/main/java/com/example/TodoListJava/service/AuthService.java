package com.example.TodoListJava.service;

import java.util.UUID;

import com.example.TodoListJava.entity.UserEntity;
import com.example.TodoListJava.repository.UserRepository;
import com.example.TodoListJava.service.exception.AuthorizationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository usuarioRepository;

    public UserEntity authenticated() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return usuarioRepository.findByEmail(username).get();
        } catch (Exception e) {
            throw new AuthorizationException("Usuário inválido");
        }
    }

    public void validateSelfOrAdmin(UUID Idusuario) {
        UserEntity usuario = authenticated();
        if (Idusuario != null && !usuario.getId().equals(Idusuario) && !usuario.hasPosition("ROLE_ADM")) {
            throw new AuthorizationException("Acesso negado");
        } else if (Idusuario == null && !usuario.hasPosition("ROLE_ADM")) {
            throw new AuthorizationException("Acesso negado");
        }
    }

    public boolean isAuthenticated() {
        try {
            authenticated();
            return true;
        } catch (AuthorizationException e) {
            return false;
        }
    }
}