package com.github.alefthallys.desafiotecnicopetize.exceptions;

public class UserAlreadyExistException extends RuntimeException {
	public UserAlreadyExistException(String message) {
		super(message);
	}
}
