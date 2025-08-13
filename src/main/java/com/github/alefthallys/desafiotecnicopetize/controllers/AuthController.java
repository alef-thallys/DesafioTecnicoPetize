package com.github.alefthallys.desafiotecnicopetize.controllers;

import com.github.alefthallys.desafiotecnicopetize.dtos.LoginRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.LoginResponseDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.RegisterRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/auth", produces = "application/json")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {
	
	private final AuthService authService;
	
	public AuthController(AuthService authService) {
		this.authService = authService;
	}
	
	@PostMapping("/login")
	@Operation(summary = "User login", description = "Authenticates a user and returns a JWT token.")
	public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
		return new ResponseEntity<>(authService.login(loginRequestDTO), HttpStatus.OK);
	}
	
	@PostMapping("/register")
	@Operation(summary = "User registration", description = "Registers a new user.")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void register(@RequestBody @Valid RegisterRequestDTO registerRequestDTO) {
		authService.register(registerRequestDTO);
	}
}
