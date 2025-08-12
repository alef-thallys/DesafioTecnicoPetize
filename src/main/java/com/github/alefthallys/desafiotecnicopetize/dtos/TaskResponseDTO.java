package com.github.alefthallys.desafiotecnicopetize.dtos;

import com.github.alefthallys.desafiotecnicopetize.enums.Priority;
import com.github.alefthallys.desafiotecnicopetize.enums.Status;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record TaskResponseDTO(
		UUID id,
		String title,
		String description,
		LocalDate dueDate,
		Status status,
		Priority priority,
		List<SubTaskResponseDTO> subTasks
) {
}
