package com.github.alefthallys.desafiotecnicopetize.exceptions;

public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException(String taskNotFound) {
		super(taskNotFound);
	}
}
