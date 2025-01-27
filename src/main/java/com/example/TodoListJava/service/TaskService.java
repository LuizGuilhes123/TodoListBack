package com.example.TodoListJava.service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.TodoListJava.dto.task.TaskDTO;
import com.example.TodoListJava.dto.task.TaskRecordDTO;
import com.example.TodoListJava.entity.TaskEntity;
import com.example.TodoListJava.entity.UserEntity;
import com.example.TodoListJava.repository.TaskRepository;
import com.example.TodoListJava.repository.UserRepository;
import com.example.TodoListJava.service.exception.DataIntegratyViolationException;
import com.example.TodoListJava.service.exception.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;
    private final UserRepository usuarioRepository;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public TaskDTO findById(Long id) {
        Optional<TaskEntity> obj = repository.findById(id);
        TaskEntity entity = obj.orElseThrow(() -> new ObjectNotFoundException("tarefa não encontrada"));
        return new TaskDTO(entity);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> listAll(UUID id) {
        List<TaskEntity> tarefas = repository.findAllByUserIdOrderByPosition(id);
        return tarefas.stream().map(tarefa -> new TaskDTO(tarefa)).collect(Collectors.toList());
    }

    @Transactional
    public TaskDTO create(TaskRecordDTO tarefaRecord, UUID id) {
        UserEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado"));
        nameAlreadyExists(tarefaRecord.name(), usuario.getId());
        TaskEntity tarefa = new TaskEntity();
        tarefa.setName(tarefaRecord.name());
        tarefa.setCost(tarefaRecord.cost());
        LocalDate dueDate = LocalDate.parse(tarefaRecord.dueDate());
        tarefa.setDueDate(dueDate);
        Integer maxPosition = repository.findMaxPositionByUser(id);
        tarefa.setPosition(maxPosition + 1);
        tarefa.setUser(usuario);
        tarefa.setFavorite(false);
        repository.save(tarefa);
        if(usuario.getNotification().equals(true)) {
            new Thread(() -> emailService.sendEmail(usuario.getEmail(), usuario.getName())).start();
        }
        return new TaskDTO(tarefa);
    }

    @Transactional
    public void update(TaskRecordDTO tarefaRecord, Long id) {
        TaskEntity tarefa = repository.findById(id).orElseThrow(() -> new ObjectNotFoundException("tarefa não encontrada"));
        nameAlreadyExists(tarefaRecord.name(), tarefa.getUser().getId());
        tarefa.setCost(tarefaRecord.cost());
        LocalDate dueDate = LocalDate.parse(tarefaRecord.dueDate());
        tarefa.setDueDate(dueDate);
        tarefa.setName(tarefaRecord.name());
        repository.save(tarefa);
    }

    @Transactional
    public void moveTask(UUID usuarioId, int sourceIndex, int destinationIndex) {
        UserEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encotrado"));
        List<TaskEntity> tarefas = repository.findAllByUserIdOrderByPosition(usuario.getId());
        TaskEntity obj = tarefas.remove(sourceIndex);
        tarefas.add(destinationIndex, obj);
        int min = sourceIndex < destinationIndex ? sourceIndex : destinationIndex;
        int max = sourceIndex < destinationIndex ? destinationIndex : sourceIndex;
        for (int i = min; i <= max; i++) {
            Long tarefaId = tarefas.get(i).getId();
            repository.updateBelongingPosition(tarefaId, i, usuarioId);
        }

    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

    @Transactional
    public TaskDTO patchUpdate(Map<String, Object> fields, Long id) {
        TaskEntity tarefa = repository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Task não encontrada"));
        if (fields.containsKey("name")) {
            String newName = (String) fields.get("name");
            nameAlreadyExists(newName, tarefa.getUser().getId());
        }
        merge(fields, tarefa);
        repository.save(tarefa);
        return new TaskDTO(tarefa);
    }

    private void merge(Map<String, Object> fields, TaskEntity tarefa) {
        fields.forEach((propertyName, propertyValue) -> {
            Field field = ReflectionUtils.findField(TaskEntity.class, propertyName);
            if (field != null) {
                field.setAccessible(true);

                Object newValue = propertyValue;
                if (field.getType().equals(BigDecimal.class)) {
                    newValue = new BigDecimal(propertyValue.toString());
                } else if (field.getType().equals(LocalDate.class)) {
                    newValue = LocalDate.parse(propertyValue.toString());
                } else if (field.getType().equals(String.class)) {
                    newValue = propertyValue.toString();
                }

                ReflectionUtils.setField(field, tarefa, newValue);
            }
        });
    }

    protected void nameAlreadyExists(String name, UUID idUser) {
        boolean exists = repository.existsByNameAndUserId(name, idUser);
        if (exists) {
            throw new DataIntegratyViolationException("O usuário já possui uma tarefa com este nome.");
        }
    }

    @Transactional
    public TaskDTO activateFavorite(Long id) {
        TaskEntity tarefa = repository.findById(id).orElseThrow(() -> new ObjectNotFoundException("tarefa não encontrada"));
        tarefa.setFavorite(!tarefa.getFavorite());
        return new TaskDTO(repository.save(tarefa));
    }

    @Transactional
    public List<TaskDTO> getFavorites(UUID usuarioId) {
        List<TaskEntity> tarefas = repository.findFavoritesByUserIdOrderByPosition(usuarioId);
        return tarefas.stream().map(TaskDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public List<TaskDTO> getTodayTasks(UUID usuarioId) {
        List<TaskEntity> tarefas = repository.findTodayTasksByUserIdOrderByPosition(usuarioId);
        return tarefas.stream().map(TaskDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public List<TaskDTO> getWeeklyTasks(UUID usuarioId) {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = now.with(DayOfWeek.SUNDAY);
        List<TaskEntity> tarefas = repository.findWeeklyTasksByUserIdOrderByPosition(usuarioId, startOfWeek, endOfWeek);
        return tarefas.stream().map(TaskDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public List<TaskDTO> getMonthlyTasks(UUID usuarioId) {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();
        List<TaskEntity> tarefas = repository.findMonthlyTasksByUserIdOrderByPosition(usuarioId, currentYear, currentMonth);
        return tarefas.stream().map(TaskDTO::new).collect(Collectors.toList());
    }
}