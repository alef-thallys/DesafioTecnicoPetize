package com.github.alefthallys.desafiotecnicopetize.security;

import com.github.alefthallys.desafiotecnicopetize.enums.Role;
import com.github.alefthallys.desafiotecnicopetize.models.UserModel;
import com.github.alefthallys.desafiotecnicopetize.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("SecurityFilter Tests")
class SecurityFilterTest {
	
	@AfterEach
	void clearContext() {
		SecurityContextHolder.clearContext();
	}
	
	@Test
	@DisplayName("Should not authenticate when Authorization header is missing")
	void testNoAuthHeader() throws Exception {
		TokenService tokenService = mock(TokenService.class);
		UserRepository userRepository = mock(UserRepository.class);
		SecurityFilter filter = new SecurityFilter(tokenService, userRepository);
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		
		when(request.getHeader("Authorization")).thenReturn(null);
		
		filter.doFilterInternal(request, response, chain);
		
		verify(chain).doFilter(request, response);
		assertNull(SecurityContextHolder.getContext().getAuthentication());
		verifyNoInteractions(tokenService, userRepository);
	}
	
	@Test
	@DisplayName("Should authenticate when valid bearer token is present")
	void testWithValidToken() throws Exception {
		TokenService tokenService = mock(TokenService.class);
		UserRepository userRepository = mock(UserRepository.class);
		SecurityFilter filter = new SecurityFilter(tokenService, userRepository);
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		
		when(request.getHeader("Authorization")).thenReturn("Bearer abc.def.ghi");
		when(tokenService.validateToken("abc.def.ghi")).thenReturn("john");
		// Return a UserModel, which implements UserDetails and matches repository signature
		UserModel userDetails = new UserModel("john", "pwd", Role.USER);
		when(userRepository.findByUsername("john")).thenReturn(userDetails);
		
		filter.doFilterInternal(request, response, chain);
		
		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
		assertEquals(userDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		verify(chain).doFilter(request, response);
	}
}
