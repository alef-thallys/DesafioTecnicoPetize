package com.github.alefthallys.desafiotecnicopetize.dtos;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(
		@NotBlank(message = "Username cannot be blank")
		String username,
		@NotBlank(message = "Password cannot be blank")
		String password) {
}

