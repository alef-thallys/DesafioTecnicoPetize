package com.github.alefthallys.desafiotecnicopetize.dtos;

import com.github.alefthallys.desafiotecnicopetize.enums.Priority;
import com.github.alefthallys.desafiotecnicopetize.enums.Status;

import java.time.LocalDate;

public record TaskResponseDTO(
		Long id,
		String title,
		String description,
		LocalDate dueDate,
		Status status,
		Priority priority
) {
}
