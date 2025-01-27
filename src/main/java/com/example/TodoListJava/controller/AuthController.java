package com.example.TodoListJava.controller;

import com.example.TodoListJava.dto.AuthenticationDTO;
import com.example.TodoListJava.dto.MessageDTO;
import com.example.TodoListJava.dto.RefreshTokenDTO;
import com.example.TodoListJava.dto.TokenResponseDTO;
import com.example.TodoListJava.entity.UserEntity;
import com.example.TodoListJava.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth")
@RestController
@RequestMapping(value = "/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    @Operation(summary = "Login da aplicação. ")
    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthenticationDTO data) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
            var authentication = authenticationManager.authenticate(usernamePassword);
            var tokens = tokenService.generateTokens((UserEntity) authentication.getPrincipal());
            return ResponseEntity.ok().body(new TokenResponseDTO(tokens.accessToken(), tokens.refreshToken()));
        } catch (InternalAuthenticationServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageDTO("Email não encontrado"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageDTO("Senha inválida"));
        }
    }

    @Operation(summary = "Refresh token da aplicação. ")
    @PostMapping(value = "/refresh-token")
    public ResponseEntity<TokenResponseDTO> refreshToken(@RequestBody RefreshTokenDTO refreshTokenDto) {
        TokenResponseDTO tokens = tokenService.refreshAccessToken(refreshTokenDto.token());
        return ResponseEntity.ok(tokens);
    }
}