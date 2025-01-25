package com.example.TodoListJava.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TaskRecordDTO(@NotBlank(message = "Campo obrigatório") @Size(max = 91, message = "máximo de caracteres é 91") String name, @NotNull(message = "Campo obrigatório") BigDecimal cost, @NotBlank(message = "Campo obrigatório") String dueDate) {

}
