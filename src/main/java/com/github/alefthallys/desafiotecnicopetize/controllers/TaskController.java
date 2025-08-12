package com.github.alefthallys.desafiotecnicopetize.controllers;

import com.github.alefthallys.desafiotecnicopetize.assemblers.TaskResponseAssembler;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskResponseDTO;
import com.github.alefthallys.desafiotecnicopetize.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/tasks", produces = "application/json")
@Tag(name = "Tasks", description = "Endpoints for managing tasks")
public class TaskController {
	
	private final TaskService taskService;
	private final TaskResponseAssembler taskResponseAssembler;
	
	public TaskController(TaskService taskService, TaskResponseAssembler taskResponseAssembler) {
		this.taskService = taskService;
		this.taskResponseAssembler = taskResponseAssembler;
	}
	
	@GetMapping
	@Operation(summary = "Get all tasks", description = "Returns a list of all tasks with HATEOAS links.")
	public ResponseEntity<CollectionModel<EntityModel<TaskResponseDTO>>> findAll() {
		List<TaskResponseDTO> tasks = taskService.findAll();
		List<EntityModel<TaskResponseDTO>> taskResources = tasks.stream()
				.map(taskResponseAssembler::toModel)
				.collect(Collectors.toList());
		CollectionModel<EntityModel<TaskResponseDTO>> collectionModel = CollectionModel.of(taskResources);
		collectionModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TaskController.class).create(null)).withRel("create"));
		return ResponseEntity.ok(collectionModel);
	}
	
	@GetMapping("/{id}")
	@Operation(summary = "Get task by ID", description = "Returns a task by its ID.")
	public ResponseEntity<EntityModel<TaskResponseDTO>> findById(@PathVariable UUID id) {
		TaskResponseDTO task = taskService.findById(id);
		EntityModel<TaskResponseDTO> resource = taskResponseAssembler.toModel(task);
		return ResponseEntity.ok(resource);
	}
	
	@PostMapping(consumes = "application/json")
	@Operation(summary = "Create a new task", description = "Creates a new task and returns it.")
	public ResponseEntity<EntityModel<TaskResponseDTO>> create(@RequestBody @Valid TaskRequestDTO taskRequestDTO) {
		TaskResponseDTO createdTask = taskService.create(taskRequestDTO);
		EntityModel<TaskResponseDTO> resource = taskResponseAssembler.toModel(createdTask);
		return ResponseEntity.status(HttpStatus.CREATED).body(resource);
	}
	
	@PutMapping(value = "/{id}", consumes = "application/json")
	@Operation(summary = "Update a task", description = "Updates an existing task by its ID.")
	public ResponseEntity<EntityModel<TaskResponseDTO>> update(@PathVariable UUID id, @RequestBody @Valid TaskRequestDTO taskRequestDTO) {
		TaskResponseDTO updatedTask = taskService.update(id, taskRequestDTO);
		EntityModel<TaskResponseDTO> resource = taskResponseAssembler.toModel(updatedTask);
		return ResponseEntity.ok(resource);
	}
	
	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a task", description = "Deletes a task by its ID.")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		taskService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
