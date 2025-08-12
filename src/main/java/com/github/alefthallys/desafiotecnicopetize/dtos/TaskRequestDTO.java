package com.github.alefthallys.desafiotecnicopetize.dtos;

import com.github.alefthallys.desafiotecnicopetize.enums.Priority;
import com.github.alefthallys.desafiotecnicopetize.enums.Status;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record TaskRequestDTO(
		
		@NotBlank(message = "Title cannot be blank")
		String title,
		
		@NotBlank(message = "Description cannot be blank")
		String description,
		
		@NotNull(message = "Due date cannot be null")
		@Future(message = "Due date must be in the future")
		LocalDate dueDate,
		
		@NotNull(message = "Status cannot be null")
		Status status,
		
		@NotNull(message = "Priority cannot be null")
		Priority priority,
		
		List<SubTaskRequestDTO> subTasks
) {
}
