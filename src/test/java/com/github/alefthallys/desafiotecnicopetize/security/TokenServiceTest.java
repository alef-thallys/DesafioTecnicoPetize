package com.github.alefthallys.desafiotecnicopetize.security;

import com.github.alefthallys.desafiotecnicopetize.enums.Role;
import com.github.alefthallys.desafiotecnicopetize.models.UserModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TokenService Tests")
class TokenServiceTest {

    @Test
    @DisplayName("generateToken and validateToken with valid secret")
    void testGenerateAndValidate() {
        TokenService service = new TokenService();
        ReflectionTestUtils.setField(service, "secret", "my-secret");
        UserModel user = new UserModel("john", "pwd", Role.USER);
        String token = service.generateToken(user);
        assertNotNull(token);
        String subject = service.validateToken(token);
        assertEquals("john", subject);
    }

    @Test
    @DisplayName("validateToken should return empty string for invalid token")
    void testValidateInvalidToken() {
        TokenService service = new TokenService();
        ReflectionTestUtils.setField(service, "secret", "my-secret");
        String subject = service.validateToken("invalid.token.value");
        assertEquals("", subject);
    }
}

