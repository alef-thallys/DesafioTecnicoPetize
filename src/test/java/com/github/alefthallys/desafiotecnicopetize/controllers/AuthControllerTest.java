package com.github.alefthallys.desafiotecnicopetize.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.alefthallys.desafiotecnicopetize.dtos.LoginRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.RegisterRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.models.UserModel;
import com.github.alefthallys.desafiotecnicopetize.repositories.UserRepository;
import com.github.alefthallys.desafiotecnicopetize.security.SecurityConfigurations;
import com.github.alefthallys.desafiotecnicopetize.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfigurations.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserModel userModel;
    private LoginRequestDTO loginRequestDTO;
    private RegisterRequestDTO registerRequestDTO;

    @BeforeEach
    void setUp() {
        userModel = new UserModel();
        userModel.setId(UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a31"));
        userModel.setUsername("testuser");
        userModel.setPassword("password");

        loginRequestDTO = new LoginRequestDTO("testuser", "password");
        registerRequestDTO = new RegisterRequestDTO("newuser", "password");
    }

    @Nested
    @DisplayName("Tests for login")
    class LoginTests {
        @Test
        @DisplayName("Should return JWT token when login is successful")
        void loginSuccess() throws Exception {
            Authentication auth = mock(Authentication.class);
            when(auth.getPrincipal()).thenReturn(userModel);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
            when(tokenService.generateToken(any(UserModel.class))).thenReturn("test-token");

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("test-token"));
        }

        @Test
        @DisplayName("Should return 403 when login fails")
        void loginFailure() throws Exception {
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new RuntimeException("Authentication failed"));

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
            when(userRepository.findByUsername(registerRequestDTO.username())).thenReturn(null);

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequestDTO)))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Should return 409 when user already exists")
        void registerUserAlreadyExists() throws Exception {
            when(userRepository.findByUsername(registerRequestDTO.username())).thenReturn(userModel);

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequestDTO)))
                    .andExpect(status().isConflict());
        }
    }
}
