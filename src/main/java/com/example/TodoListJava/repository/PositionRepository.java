package com.example.TodoListJava.repository;

import com.example.TodoListJava.entity.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface PositionRepository extends JpaRepository<PositionEntity, Long> {

}