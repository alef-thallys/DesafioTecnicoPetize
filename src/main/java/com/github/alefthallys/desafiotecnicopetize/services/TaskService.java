package com.github.alefthallys.desafiotecnicopetize.services;

import com.github.alefthallys.desafiotecnicopetize.dtos.TaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskResponseDTO;
import com.github.alefthallys.desafiotecnicopetize.exceptions.AccessDeniedTaskException;
import com.github.alefthallys.desafiotecnicopetize.exceptions.ResourceNotFoundException;
import com.github.alefthallys.desafiotecnicopetize.models.Task;
import com.github.alefthallys.desafiotecnicopetize.models.User;
import com.github.alefthallys.desafiotecnicopetize.repositories.TaskRepository;
import com.github.alefthallys.desafiotecnicopetize.repositories.UserRepository;
import com.github.alefthallys.desafiotecnicopetize.utils.TaskMapperUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {
	
	private final TaskRepository taskRepository;
	private final UserRepository userRepository;
	
	public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
		this.taskRepository = taskRepository;
		this.userRepository = userRepository;
	}
	
	private User getCurrentUser() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByUsername(username);
		if (user == null) {
			throw new ResourceNotFoundException("User not found");
		}
		return user;
	}
	
	public List<TaskResponseDTO> findAll() {
		User currentUser = getCurrentUser();
		return taskRepository.findByUserId(currentUser.getId()).stream().map(TaskMapperUtils::toResponseDTO).toList();
	}
	
	public TaskResponseDTO findById(UUID id) {
		User currentUser = getCurrentUser();
		Task task = taskRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
		if (!task.getUser().getId().equals(currentUser.getId())) {
			throw new AccessDeniedTaskException("You do not have permission to view this task.");
		}
		return TaskMapperUtils.toResponseDTO(task);
	}
	
	public TaskResponseDTO create(TaskRequestDTO taskRequestDTO) {
		User currentUser = getCurrentUser();
		Task task = TaskMapperUtils.toEntity(taskRequestDTO);
		task.setUser(currentUser);
		return TaskMapperUtils.toResponseDTO(taskRepository.save(task));
	}
	
	public TaskResponseDTO update(UUID id, TaskRequestDTO taskRequestDTO) {
		User currentUser = getCurrentUser();
		Task existingTask = taskRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
		
		if (!existingTask.getUser().getId().equals(currentUser.getId())) {
			throw new AccessDeniedTaskException("You do not have permission to update this task.");
		}
		
		existingTask.setTitle(taskRequestDTO.title());
		existingTask.setDescription(taskRequestDTO.description());
		existingTask.setDueDate(taskRequestDTO.dueDate());
		existingTask.setStatus(taskRequestDTO.status());
		existingTask.setPriority(taskRequestDTO.priority());
		
		existingTask.getSubTasks().clear();
		if (taskRequestDTO.subTasks() != null) {
			taskRequestDTO.subTasks().stream()
					.map(subTaskDTO -> {
						var subTask = new com.github.alefthallys.desafiotecnicopetize.models.SubTask();
						subTask.setTitle(subTaskDTO.title());
						subTask.setStatus(subTaskDTO.status());
						subTask.setTask(existingTask);
						return subTask;
					})
					.forEach(existingTask.getSubTasks()::add);
		}
		
		return TaskMapperUtils.toResponseDTO(taskRepository.save(existingTask));
	}
	
	public void delete(UUID id) {
		User currentUser = getCurrentUser();
		Task task = taskRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
		
		if (!task.getUser().getId().equals(currentUser.getId())) {
			throw new AccessDeniedTaskException("You do not have permission to delete this task.");
		}
		
		boolean hasTodoSubTask = task.getSubTasks() != null && task.getSubTasks().stream()
				.anyMatch(subTask -> subTask.getStatus() == com.github.alefthallys.desafiotecnicopetize.enums.Status.TODO);
		if (hasTodoSubTask) {
			throw new IllegalStateException("Cannot delete a Task with SubTasks in TODO status");
		}
		taskRepository.deleteById(id);
	}
}
