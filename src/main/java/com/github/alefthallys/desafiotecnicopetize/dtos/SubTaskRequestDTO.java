package com.github.alefthallys.desafiotecnicopetize.dtos;

import com.github.alefthallys.desafiotecnicopetize.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubTaskRequestDTO(
		@NotBlank
		String title,
		@NotNull
		Status status
) {
}

