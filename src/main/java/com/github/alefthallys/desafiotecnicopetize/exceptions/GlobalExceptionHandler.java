package com.github.alefthallys.desafiotecnicopetize.exceptions;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
		ErrorResponse error = new ErrorResponse(
				LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
				HttpStatus.NOT_FOUND.value(),
				"Resource Not Found",
				ex.getMessage()
		);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler(AccessDeniedTaskException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedTaskException ex) {
		ErrorResponse error = new ErrorResponse(
				LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
				HttpStatus.FORBIDDEN.value(),
				"Access Denied",
				ex.getMessage()
		);
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
	}
	
	@ExceptionHandler(UserAlreadyExistException.class)
	public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistException ex) {
		ErrorResponse error = new ErrorResponse(
				LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
				HttpStatus.CONFLICT.value(),
				"User Already Exists",
				ex.getMessage()
		);
		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
		ErrorResponse error = new ErrorResponse(
				LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
				HttpStatus.BAD_REQUEST.value(),
				"Bad Request",
				ex.getMessage()
		);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
		String message = "Invalid request body.";
		Throwable cause = ex.getCause();
		
		if (cause instanceof InvalidFormatException invalidFormatException) {
			Class<?> clazz = invalidFormatException.getTargetType();
			Object invalidValue = invalidFormatException.getValue();
			
			List<Reference> path = invalidFormatException.getPath();
			String fieldName = path.stream()
					.map(ref -> {
						if (ref.getFieldName() != null) {
							return ref.getFieldName();
						} else if (ref.getIndex() != -1) {
							return "[" + ref.getIndex() + "]";
						} else {
							return "?";
						}
					}).collect(Collectors.joining("."));
			
			if (clazz.isEnum()) {
				Object[] validValues = clazz.getEnumConstants();
				String validOptions = java.util.Arrays.stream(validValues)
						.map(Object::toString)
						.collect(Collectors.joining(", "));
				message = String.format("The field '%s' has an invalid value '%s'. Valid values are: [%s]",
						fieldName, invalidValue, validOptions);
			} else {
				message = String.format("Invalid value '%s' for field '%s'.", invalidValue, fieldName);
			}
		} else {
			message += " " + ex.getMessage();
		}
		
		ErrorResponse error = new ErrorResponse(
				LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
				HttpStatus.BAD_REQUEST.value(),
				"Bad Request",
				message
		);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
		ErrorResponse error = new ErrorResponse(
				LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
				HttpStatus.BAD_REQUEST.value(),
				"Illegal state",
				ex.getMessage()
		);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> String.format("Field '%s': %s", error.getField(), error.getDefaultMessage()))
				.collect(Collectors.joining("; "));
		
		ErrorResponse error = new ErrorResponse(
				LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
				HttpStatus.BAD_REQUEST.value(),
				"Validation Failed",
				message
		);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	public record ErrorResponse(
			String timestamp,
			int status,
			String error,
			String message
	) {
	}
}
