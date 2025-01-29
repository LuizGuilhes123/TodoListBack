package com.example.TodoListJava.config;

import java.io.IOException;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.TodoListJava.entity.UserEntity;
import com.example.TodoListJava.entity.details.CustomUserDetails;
import com.example.TodoListJava.repository.UserRepository;
import com.example.TodoListJava.service.TokenService;
import com.example.TodoListJava.service.exception.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.TokenExpiredException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final UserRepository usuarioRepository;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token;
        String authorizationHeader = request.getHeader("Authorization") == null ? request.getParameter("Authorization")
                : request.getHeader("Authorization");
        try {
            if (authorizationHeader != null) {
                token = authorizationHeader.replace("Bearer ", "");
                String email = tokenService.validacaoToken(token); // Valida o token

                // Busca o usuário e verifica se existe
                UserEntity usuario = usuarioRepository.findByEmail(email)
                        .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado"));
                CustomUserDetails customUserDetails = new CustomUserDetails(usuario);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        customUserDetails, null, customUserDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (TokenExpiredException ex) {
            // Token expirado
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Token expirado. Faça login novamente.");
        } catch (JWTVerificationException ex) {
            // Token inválido
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Token inválido. Verifique suas credenciais.");
        } catch (Exception ex) {
            // Outros erros
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write("Erro interno do servidor: " + ex.getMessage());
        }
    }
}
