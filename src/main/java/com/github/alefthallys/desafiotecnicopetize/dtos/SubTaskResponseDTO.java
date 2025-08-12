package com.github.alefthallys.desafiotecnicopetize.dtos;

import com.github.alefthallys.desafiotecnicopetize.enums.Status;

import java.time.LocalDateTime;
import java.util.UUID;

public record SubTaskResponseDTO(
		UUID id,
		String title,
		Status status,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {
}

