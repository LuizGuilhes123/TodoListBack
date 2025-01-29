package com.example.TodoListJava.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.example.TodoListJava.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    @Query("SELECT COALESCE(MAX(t.position), -1) FROM TaskEntity t WHERE t.user.id = :usuarioId")
    Integer findMaxPositionByUser(@Param("usuarioId") UUID usuarioId);

    List<TaskEntity> findAllByUserIdOrderByPosition(UUID usuarioId);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE tb_task t SET t.position = :newPosition WHERE t.id = :id AND t.user_id = :usuarioId")
    void updateBelongingPosition(Long id, Integer newPosition, UUID usuarioId);

    boolean existsByNameAndUserId(String name, UUID usuarioId);

    @Query("SELECT t FROM TaskEntity t WHERE t.user.id = :usuarioId AND t.favorite = true ORDER BY t.position")
    List<TaskEntity> findFavoritesByUserIdOrderByPosition(@Param("usuarioId") UUID usuarioId);

    @Query("SELECT t FROM TaskEntity t WHERE t.user.id = :usuarioId AND t.dueDate = CURRENT_DATE ORDER BY t.position")
    List<TaskEntity> findTodayTasksByUserIdOrderByPosition(@Param("usuarioId") UUID usuarioId);

    @Query("SELECT t FROM TaskEntity t WHERE t.user.id = :usuarioId AND t.dueDate BETWEEN :startOfWeek AND :endOfWeek ORDER BY t.position")
    List<TaskEntity> findWeeklyTasksByUserIdOrderByPosition(
            @Param("usuarioId") UUID usuarioId,
            @Param("startOfWeek") LocalDate startOfWeek,
            @Param("endOfWeek") LocalDate endOfWeek
    );

    @Query("SELECT t FROM TaskEntity t WHERE t.user.id = :usuarioId AND YEAR(t.dueDate) = :year AND MONTH(t.dueDate) = :month ORDER BY t.position")
    List<TaskEntity> findMonthlyTasksByUserIdOrderByPosition(
            @Param("usuarioId") UUID usuarioId,
            @Param("year") int year,
            @Param("month") int month
    );
}