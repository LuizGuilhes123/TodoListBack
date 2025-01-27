package com.example.TodoListJava.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

import com.example.TodoListJava.dto.AuthenticationDTO;
import com.example.TodoListJava.dto.TokenResponseDTO;
import com.example.TodoListJava.entity.PositionEntity;
import com.example.TodoListJava.entity.UserEntity;
import com.example.TodoListJava.entity.details.CustomUserDetails;
import com.example.TodoListJava.repository.UserRepository;
import com.example.TodoListJava.service.exception.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService implements UserDetailsService {

    @Value("${auth.jwt.token.secret}")
    private String secret;

    @Value("${auth.jwt.token.expiration}")
    private Integer horaExpiracaoToken;

    @Value("${auth.jwt.refresh-token.expiration}")
    private Integer horaExpiracaoRefreshToken;

    private final UserRepository usuarioRepository;

    private static final String ISSUER = "Tarefas";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = usuarioRepository.findByEmail(username).orElseThrow(() ->
                new ObjectNotFoundException("Email não encontrado"));
        return new CustomUserDetails(user);
    }


    public TokenResponseDTO obterToken(AuthenticationDTO authDto) {
        UserEntity user = usuarioRepository.findByEmail(authDto.email()).get();
        if (user == null) {
            throw new UsernameNotFoundException("Obter Token falhou");
        }
        return generateTokens(user);
    }

    public TokenResponseDTO generateTokens(UserEntity usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String rolesAsString = String.join(",",
                    usuario.getCargos().stream().map(PositionEntity::getAuthority).collect(Collectors.toList()));

            String accessToken = JWT.create().withIssuer(ISSUER).withSubject(usuario.getEmail())
                    .withClaim("id", usuario.getId().toString()).withClaim("nome", usuario.getName())
                    .withClaim("email", usuario.getEmail()).withClaim("roles", rolesAsString)
                    .withClaim("notification", usuario.getNotification())
                    .withExpiresAt(genExpiInstance()).sign(algorithm);

            String refreshToken = JWT.create().withIssuer(ISSUER).withSubject(usuario.getEmail())
                    .withExpiresAt(genRefreshTokenExpiInstance()).sign(algorithm);

            return new TokenResponseDTO(accessToken, refreshToken);
        } catch (JWTCreationException e) {
            throw new RuntimeException("Erro na hora de gerar os tokens");
        }
    }

    public String validacaoToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm).withIssuer(ISSUER).build().verify(token).getSubject();
        } catch (JWTCreationException e) {
            throw new IllegalArgumentException("Problema na hora de validar o token");
        }
    }

    private Instant genExpiInstance() {
        return LocalDateTime.now().plusHours(horaExpiracaoToken).toInstant(ZoneOffset.of("-03:00"));
    }

    private Instant genRefreshTokenExpiInstance() {
        return LocalDateTime.now().plusHours(horaExpiracaoRefreshToken).toInstant(ZoneOffset.of("-03:00"));
    }

    public TokenResponseDTO refreshAccessToken(String refreshToken) {
        try {

            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUER).acceptExpiresAt(3600).build();
            DecodedJWT jwt = verifier.verify(refreshToken);

            String email = jwt.getSubject();

            UserEntity usuario = usuarioRepository.findByEmail(email).get();
            if (usuario == null) {
                throw new ObjectNotFoundException("Email não encontrado");
            }

            return generateTokens(usuario);
        } catch (JWTVerificationException e) {
            if (e instanceof TokenExpiredException) {
                Algorithm.HMAC256(secret);
                DecodedJWT jwt = JWT.decode(refreshToken);
                String email = jwt.getSubject();

                UserEntity usuario = usuarioRepository.findByEmail(email).get();
                if (usuario == null) {
                    throw new ObjectNotFoundException("Email não encontrado");
                }

                return generateTokens(usuario);
            } else {
                throw new IllegalArgumentException("Refresh token inválido");
            }
        }
    }
}
