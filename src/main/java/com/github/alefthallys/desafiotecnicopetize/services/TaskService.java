package com.github.alefthallys.desafiotecnicopetize.services;

import com.github.alefthallys.desafiotecnicopetize.dtos.TaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskResponseDTO;
import com.github.alefthallys.desafiotecnicopetize.enums.Priority;
import com.github.alefthallys.desafiotecnicopetize.enums.Status;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {
	
	public List<TaskResponseDTO> findAll() {
		return List.of(new TaskResponseDTO(
				1L,
				"Sample Task",
				"Sample Description",
				LocalDate.now(),
				Status.TODO,
				Priority.MEDIUM)
		);
	}
	
	public TaskResponseDTO findById(Long id) {
		return new TaskResponseDTO(
				1L,
				"Sample Task",
				"Sample Description",
				LocalDate.now(),
				Status.DONE,
				Priority.HIGH
		);
	}
	
	public TaskResponseDTO create(TaskRequestDTO taskRequestDTO) {
		return new TaskResponseDTO(
				1L,
				taskRequestDTO.title(),
				taskRequestDTO.description(),
				taskRequestDTO.dueDate(),
				taskRequestDTO.status(),
				taskRequestDTO.priority()
		);
	}
	
	public TaskResponseDTO update(Long id, TaskRequestDTO taskRequestDTO) {
		return new TaskResponseDTO(
				1L,
				taskRequestDTO.title(),
				taskRequestDTO.description(),
				taskRequestDTO.dueDate(),
				taskRequestDTO.status(),
				taskRequestDTO.priority()
		);
	}
	
	public Void delete(Long id) {
		return null;
	}
}
