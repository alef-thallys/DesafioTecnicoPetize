package com.github.alefthallys.desafiotecnicopetize.services;

import com.github.alefthallys.desafiotecnicopetize.dtos.LoginRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.LoginResponseDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.RegisterRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.enums.Role;
import com.github.alefthallys.desafiotecnicopetize.exceptions.InvalidCredentials;
import com.github.alefthallys.desafiotecnicopetize.exceptions.UserAlreadyExistException;
import com.github.alefthallys.desafiotecnicopetize.models.UserModel;
import com.github.alefthallys.desafiotecnicopetize.repositories.UserRepository;
import com.github.alefthallys.desafiotecnicopetize.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class AuthService {
	
	private final AuthenticationManager authenticationManager;
	private final UserRepository repository;
	private final TokenService tokenService;
	
	public AuthService(AuthenticationManager authenticationManager, UserRepository repository, TokenService tokenService) {
		this.authenticationManager = authenticationManager;
		this.repository = repository;
		this.tokenService = tokenService;
	}
	
	public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
		try {
			UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(loginRequestDTO.username(), loginRequestDTO.password());
			Authentication auth = this.authenticationManager.authenticate(usernamePassword);
			String token = tokenService.generateToken((UserModel) auth.getPrincipal());
			return new LoginResponseDTO(token);
		} catch (RuntimeException e) {
			throw new InvalidCredentials("Invalid username or password: " + loginRequestDTO.username());
		}
	}
	
	public void register(@RequestBody @Valid RegisterRequestDTO registerRequestDTO) {
		if (repository.findByUsername(registerRequestDTO.username()) != null) {
			throw new UserAlreadyExistException("User already exists with username: " + registerRequestDTO.username());
		}
		
		String encryptedPassword = new BCryptPasswordEncoder().encode(registerRequestDTO.password());
		UserModel newUserModel = new UserModel(registerRequestDTO.username(), encryptedPassword, Role.USER);
		repository.save(newUserModel);
	}
}
