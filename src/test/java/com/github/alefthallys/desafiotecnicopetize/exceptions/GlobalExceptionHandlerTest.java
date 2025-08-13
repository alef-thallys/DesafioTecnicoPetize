package com.github.alefthallys.desafiotecnicopetize.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.enums.Priority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("HttpMessageNotReadable - enum invalid value path")
    void testHttpMessageNotReadableEnumPath() {
        InvalidFormatException ife = InvalidFormatException.from(null, "enum invalid", "WRONG", Priority.class);
        ife.prependPath(TaskRequestDTO.class, "status");
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Invalid body", ife);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> resp = handler.handleHttpMessageNotReadable(ex);
        assertEquals(400, resp.getStatusCode().value());
        assertTrue(resp.getBody().message().contains("status"));
        assertTrue(resp.getBody().message().contains("WRONG"));
        assertTrue(resp.getBody().message().contains("Valid values"));
    }

    @Test
    @DisplayName("HttpMessageNotReadable - non-enum invalid value path")
    void testHttpMessageNotReadableNonEnumPath() {
        InvalidFormatException ife = InvalidFormatException.from(null, "date invalid", "2025-13-40", LocalDate.class);
        ife.prependPath(TaskRequestDTO.class, "dueDate");
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Invalid body", ife);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> resp = handler.handleHttpMessageNotReadable(ex);
        assertEquals(400, resp.getStatusCode().value());
        assertTrue(resp.getBody().message().contains("dueDate"));
        assertTrue(resp.getBody().message().contains("2025-13-40"));
        assertFalse(resp.getBody().message().contains("Valid values"));
    }

    @Test
    @DisplayName("HttpMessageNotReadable - other cause")
    void testHttpMessageNotReadableOtherCause() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Boom", new RuntimeException("root"));
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> resp = handler.handleHttpMessageNotReadable(ex);
        assertEquals(400, resp.getStatusCode().value());
        assertTrue(resp.getBody().message().startsWith("Invalid request body."));
    }

    @Test
    @DisplayName("AccessDenied and IllegalState mapping")
    void testSimpleMappings() {
        var denied = handler.handleAccessDenied(new AccessDeniedTaskException("nope"));
        assertEquals(403, denied.getStatusCode().value());
        assertEquals("nope", denied.getBody().message());

        var illegal = handler.handleIllegalState(new IllegalStateException("bad"));
        assertEquals(400, illegal.getStatusCode().value());
        assertEquals("bad", illegal.getBody().message());
    }
}

