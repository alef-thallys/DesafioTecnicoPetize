package com.github.alefthallys.desafiotecnicopetize.services;

import com.github.alefthallys.desafiotecnicopetize.dtos.SubTaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskResponseDTO;
import com.github.alefthallys.desafiotecnicopetize.exceptions.AccessDeniedTaskException;
import com.github.alefthallys.desafiotecnicopetize.exceptions.ResourceNotFoundException;
import com.github.alefthallys.desafiotecnicopetize.models.SubTaskModel;
import com.github.alefthallys.desafiotecnicopetize.models.TaskModel;
import com.github.alefthallys.desafiotecnicopetize.models.UserModel;
import com.github.alefthallys.desafiotecnicopetize.repositories.TaskRepository;
import com.github.alefthallys.desafiotecnicopetize.repositories.UserRepository;
import com.github.alefthallys.desafiotecnicopetize.utils.TaskMapperUtils;
import jakarta.validation.Valid;
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
	
	private UserModel getCurrentUser() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		UserModel userModel = userRepository.findByUsername(username);
		if (userModel == null) {
			throw new ResourceNotFoundException("User not found");
		}
		return userModel;
	}
	
	public List<TaskResponseDTO> findAll() {
		UserModel currentUserModel = getCurrentUser();
		return taskRepository.findByUserModelId(currentUserModel.getId()).stream().map(TaskMapperUtils::toResponseDTO).toList();
	}
	
	public TaskResponseDTO findById(UUID id) {
		UserModel currentUserModel = getCurrentUser();
		TaskModel taskModel = taskRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
		if (!taskModel.getUserModel().getId().equals(currentUserModel.getId())) {
			throw new AccessDeniedTaskException("You do not have permission to view this task.");
		}
		return TaskMapperUtils.toResponseDTO(taskModel);
	}
	
	public TaskResponseDTO create(TaskRequestDTO taskRequestDTO) {
		UserModel currentUserModel = getCurrentUser();
		TaskModel taskModel = TaskMapperUtils.toEntity(taskRequestDTO);
		taskModel.setUserModel(currentUserModel);
		return TaskMapperUtils.toResponseDTO(taskRepository.save(taskModel));
	}
	
	public TaskResponseDTO update(UUID id, TaskRequestDTO taskRequestDTO) {
		UserModel currentUserModel = getCurrentUser();
		TaskModel existingTaskModel = taskRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
		
		if (!existingTaskModel.getUserModel().getId().equals(currentUserModel.getId())) {
			throw new AccessDeniedTaskException("You do not have permission to update this task.");
		}
		
		existingTaskModel.setTitle(taskRequestDTO.title());
		existingTaskModel.setDescription(taskRequestDTO.description());
		existingTaskModel.setDueDate(taskRequestDTO.dueDate());
		existingTaskModel.setStatus(taskRequestDTO.status());
		existingTaskModel.setPriority(taskRequestDTO.priority());
		
		existingTaskModel.getSubTaskModels().clear();
		if (taskRequestDTO.subTasks() != null) {
			taskRequestDTO.subTasks().stream()
					.map(subTaskDTO -> {
						var subTask = new SubTaskModel();
						subTask.setTitle(subTaskDTO.title());
						subTask.setStatus(subTaskDTO.status());
						subTask.setTaskModel(existingTaskModel);
						return subTask;
					})
					.forEach(existingTaskModel.getSubTaskModels()::add);
		}
		
		return TaskMapperUtils.toResponseDTO(taskRepository.save(existingTaskModel));
	}
	
	public void delete(UUID id) {
		UserModel currentUserModel = getCurrentUser();
		TaskModel taskModel = taskRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
		
		if (!taskModel.getUserModel().getId().equals(currentUserModel.getId())) {
			throw new AccessDeniedTaskException("You do not have permission to delete this task.");
		}
		
		boolean hasTodoSubTask = taskModel.getSubTaskModels() != null && taskModel.getSubTaskModels().stream()
				.anyMatch(subTaskModel -> subTaskModel.getStatus() == com.github.alefthallys.desafiotecnicopetize.enums.Status.TODO);
		if (hasTodoSubTask) {
			throw new IllegalStateException("Cannot delete a Task with SubTasks in TODO status");
		}
		taskRepository.deleteById(id);
	}
}
