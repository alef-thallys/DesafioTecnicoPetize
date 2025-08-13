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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {
	
	@Mock
	private AuthenticationManager authenticationManager;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private TokenService tokenService;
	
	@InjectMocks
	private AuthService authService;
	
	@Test
	@DisplayName("login - success returns token")
	void loginSuccess() {
		LoginRequestDTO req = new LoginRequestDTO("john", "pwd");
		Authentication auth = mock(Authentication.class);
		UserModel principal = new UserModel("john", "enc", Role.USER);
		
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
		when(auth.getPrincipal()).thenReturn(principal);
		when(tokenService.generateToken(principal)).thenReturn("jwt-token");
		
		LoginResponseDTO resp = authService.login(req);
		assertNotNull(resp);
		assertEquals("jwt-token", resp.token());
		
		verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
		verify(tokenService).generateToken(principal);
	}
	
	@Test
	@DisplayName("login - throws InvalidCredentials on auth error")
	void loginFailure() {
		LoginRequestDTO req = new LoginRequestDTO("john", "bad");
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenThrow(new BadCredentialsException("bad"));
		
		assertThrows(InvalidCredentials.class, () -> authService.login(req));
	}
	
	@Test
	@DisplayName("register - success saves encoded user with ROLE_USER")
	void registerSuccess() {
		RegisterRequestDTO req = new RegisterRequestDTO("newuser", "secret");
		when(userRepository.findByUsername("newuser")).thenReturn(null);
		
		authService.register(req);
		
		ArgumentCaptor<UserModel> captor = ArgumentCaptor.forClass(UserModel.class);
		verify(userRepository).save(captor.capture());
		UserModel saved = captor.getValue();
		assertEquals("newuser", saved.getUsername());
		assertNotEquals("secret", saved.getPassword());
		assertEquals(Role.USER, saved.getRole());
	}
	
	@Test
	@DisplayName("register - throws when username already exists")
	void registerUserAlreadyExists() {
		RegisterRequestDTO req = new RegisterRequestDTO("john", "pwd");
		when(userRepository.findByUsername("john")).thenReturn(new UserModel());
		
		assertThrows(UserAlreadyExistException.class, () -> authService.register(req));
		verify(userRepository, never()).save(any());
	}
}

