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

		existingTask.setSubTasks(new java.util.ArrayList<>(existingTask.getSubTasks()));
		existingTask.getSubTasks().clear();
		if (taskRequestDTO.subTasks() != null) {
			for (var subTaskDTO : taskRequestDTO.subTasks()) {
				var subTask = new com.github.alefthallys.desafiotecnicopetize.models.SubTask();
				subTask.setTitle(subTaskDTO.title());
				subTask.setStatus(subTaskDTO.status());
				subTask.setTask(existingTask);
				existingTask.getSubTasks().add(subTask);
			}
		}
		
		return TaskMapperUtils.toResponseDTO(taskRepository.save(existingTask));
	}
	
	public void delete(UUID id) {
		Task task = taskRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
		boolean hasTodoSubTask = task.getSubTasks() != null && task.getSubTasks().stream()
				.anyMatch(subTask -> subTask.getStatus() == com.github.alefthallys.desafiotecnicopetize.enums.Status.TODO);
		if (hasTodoSubTask) {
			throw new IllegalStateException("Cannot delete a Task with SubTasks in TODO status");
		}
		taskRepository.deleteById(id);
	}
}
