package com.github.alefthallys.desafiotecnicopetize.services;

import com.github.alefthallys.desafiotecnicopetize.dtos.TaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskResponseDTO;
import com.github.alefthallys.desafiotecnicopetize.exceptions.ResourceNotFoundException;
import com.github.alefthallys.desafiotecnicopetize.models.Task;
import com.github.alefthallys.desafiotecnicopetize.repositories.TaskRepository;
import com.github.alefthallys.desafiotecnicopetize.utils.TaskMapperUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {
	
	private final TaskRepository taskRepository;
	
	public TaskService(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}
	
	public List<TaskResponseDTO> findAll() {
		return taskRepository.findAll().stream().map(TaskMapperUtils::toResponseDTO).toList();
	}
	
	public TaskResponseDTO findById(UUID id) {
		return taskRepository.findById(id)
				.map(TaskMapperUtils::toResponseDTO)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
	}
	
	public TaskResponseDTO create(TaskRequestDTO taskRequestDTO) {
		Task task = TaskMapperUtils.toEntity(taskRequestDTO);
		return TaskMapperUtils.toResponseDTO(taskRepository.save(task));
	}
	
	public TaskResponseDTO update(UUID id, TaskRequestDTO taskRequestDTO) {
		Task existingTask = taskRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
		
		existingTask.setTitle(taskRequestDTO.title());
		existingTask.setDescription(taskRequestDTO.description());
		existingTask.setDueDate(taskRequestDTO.dueDate());
		existingTask.setStatus(taskRequestDTO.status());
		existingTask.setPriority(taskRequestDTO.priority());
		
		return TaskMapperUtils.toResponseDTO(taskRepository.save(existingTask));
	}
	
	public void delete(UUID id) {
		if (!taskRepository.existsById(id)) {
			throw new ResourceNotFoundException("Task not found with id: " + id);
		}
		
		taskRepository.deleteById(id);
	}
}
