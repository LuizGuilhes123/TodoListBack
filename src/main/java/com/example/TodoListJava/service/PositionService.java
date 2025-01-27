package com.example.TodoListJava.service;

import java.util.Optional;

import com.example.TodoListJava.dto.position.PositionDTO;
import com.example.TodoListJava.entity.PositionEntity;
import com.example.TodoListJava.repository.PositionRepository;
import com.example.TodoListJava.service.exception.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepository repository;

    @Transactional(readOnly = true)
    public PositionDTO findById(Long id) {
        Optional<PositionEntity> obj = repository.findById(id);
        PositionEntity entity = obj.orElseThrow(() -> new ObjectNotFoundException("cargo não encontrado"));
        return new PositionDTO(entity);
    }

    @Transactional
    public PositionDTO create(PositionDTO cargoDTO) {
        PositionEntity cargo = new PositionEntity();
        cargo.setAuthority(cargoDTO.getAuthority());
        repository.save(cargo);
        return new PositionDTO(cargo);
    }

}