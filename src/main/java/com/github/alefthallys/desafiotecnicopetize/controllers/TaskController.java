package com.github.alefthallys.desafiotecnicopetize.controllers;

import com.github.alefthallys.desafiotecnicopetize.dtos.TaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskResponseDTO;
import com.github.alefthallys.desafiotecnicopetize.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
	
	private final TaskService taskService;
	
	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}
	
	@GetMapping
	public ResponseEntity<List<TaskResponseDTO>> findAll() {
		return new ResponseEntity<>(taskService.findAll(), HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<TaskResponseDTO> findById(@PathVariable UUID id) {
		return new ResponseEntity<>(taskService.findById(id), HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<TaskResponseDTO> create(@RequestBody @Valid TaskRequestDTO taskRequestDTO) {
		return new ResponseEntity<>(taskService.create(taskRequestDTO), HttpStatus.CREATED);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<TaskResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid TaskRequestDTO taskRequestDTO) {
		return new ResponseEntity<>(taskService.update(id, taskRequestDTO), HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		taskService.delete(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
