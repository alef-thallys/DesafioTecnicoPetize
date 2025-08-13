package com.github.alefthallys.desafiotecnicopetize.controllers;

import com.github.alefthallys.desafiotecnicopetize.dtos.LoginRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.LoginResponseDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.RegisterRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.enums.Role;
import com.github.alefthallys.desafiotecnicopetize.exceptions.UserAlreadyExistException;
import com.github.alefthallys.desafiotecnicopetize.models.UserModel;
import com.github.alefthallys.desafiotecnicopetize.repositories.UserRepository;
import com.github.alefthallys.desafiotecnicopetize.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/auth", produces = "application/json")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {
	
	private final AuthenticationManager authenticationManager;
	private final UserRepository repository;
	private final TokenService tokenService;
	
	public AuthController(AuthenticationManager authenticationManager, UserRepository repository, TokenService tokenService) {
		this.authenticationManager = authenticationManager;
		this.repository = repository;
		this.tokenService = tokenService;
	}
	
	@PostMapping("/login")
	@Operation(summary = "User login", description = "Authenticates a user and returns a JWT token.")
	public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
		try {
			UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(loginRequestDTO.username(), loginRequestDTO.password());
			Authentication auth = this.authenticationManager.authenticate(usernamePassword);
			String token = tokenService.generateToken((UserModel) auth.getPrincipal());
			return ResponseEntity.ok(new LoginResponseDTO(token));
		} catch (RuntimeException e) {
			return ResponseEntity.status(403).build();
		}
	}
	
	@PostMapping("/register")
	@Operation(summary = "User registration", description = "Registers a new user.")
	public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequestDTO registerRequestDTO) {
		if (repository.findByUsername(registerRequestDTO.username()) != null) {
			throw new UserAlreadyExistException("User already exists with username: " + registerRequestDTO.username());
		}
		
		String encryptedPassword = new BCryptPasswordEncoder().encode(registerRequestDTO.password());
		UserModel newUserModel = new UserModel(registerRequestDTO.username(), encryptedPassword, Role.USER);
		this.repository.save(newUserModel);
		return ResponseEntity.noContent().build();
	}
}
