package com.github.alefthallys.desafiotecnicopetize.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.alefthallys.desafiotecnicopetize.dtos.LoginRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.LoginResponseDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.RegisterRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.exceptions.GlobalExceptionHandler;
import com.github.alefthallys.desafiotecnicopetize.exceptions.InvalidCredentials;
import com.github.alefthallys.desafiotecnicopetize.exceptions.UserAlreadyExistException;
import com.github.alefthallys.desafiotecnicopetize.security.SecurityFilter;
import com.github.alefthallys.desafiotecnicopetize.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
		controllers = AuthController.class,
		excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private AuthService authService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private LoginRequestDTO loginRequestDTO;
	private RegisterRequestDTO registerRequestDTO;
	
	@BeforeEach
	void setUp() {
		loginRequestDTO = new LoginRequestDTO("testuser", "password");
		registerRequestDTO = new RegisterRequestDTO("newuser", "password");
	}
	
	@Nested
	@DisplayName("Tests for login")
	class LoginTests {
		@Test
		@DisplayName("Should return JWT token when login is successful")
		void loginSuccess() throws Exception {
			when(authService.login(any(LoginRequestDTO.class))).thenReturn(new LoginResponseDTO("test-token"));
			
			mockMvc.perform(post("/api/v1/auth/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(loginRequestDTO)))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.token").value("test-token"));
		}
		
		@Test
		@DisplayName("Should return 403 when login fails")
		void loginFailure() throws Exception {
			when(authService.login(any(LoginRequestDTO.class)))
					.thenThrow(new InvalidCredentials("Invalid username or password"));
			
			mockMvc.perform(post("/api/v1/auth/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(loginRequestDTO)))
					.andExpect(status().isForbidden());
		}
	}
	
	@Nested
	@DisplayName("Tests for register")
	class RegisterTests {
		@Test
		@DisplayName("Should register a new user successfully")
		void registerSuccess() throws Exception {
			mockMvc.perform(post("/api/v1/auth/register")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(registerRequestDTO)))
					.andExpect(status().isNoContent());
		}
		
		@Test
		@DisplayName("Should return 409 when user already exists")
		void registerUserAlreadyExists() throws Exception {
			doThrow(new UserAlreadyExistException("User already exists"))
					.when(authService).register(any(RegisterRequestDTO.class));
			
			mockMvc.perform(post("/api/v1/auth/register")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(registerRequestDTO)))
					.andExpect(status().isConflict());
		}
	}
}
