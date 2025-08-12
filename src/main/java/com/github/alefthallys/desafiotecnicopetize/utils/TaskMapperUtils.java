package com.github.alefthallys.desafiotecnicopetize.utils;


import com.github.alefthallys.desafiotecnicopetize.dtos.TaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskResponseDTO;
import com.github.alefthallys.desafiotecnicopetize.models.Task;

public class TaskMapperUtils {
	
	public static Task toEntity(TaskRequestDTO dto) {
		Task task = new Task();
		task.setTitle(dto.title());
		task.setDescription(dto.description());
		task.setDueDate(dto.dueDate());
		task.setStatus(dto.status());
		task.setPriority(dto.priority());
		return task;
	}
	
	public static TaskResponseDTO toResponseDTO(Task task) {
		return new TaskResponseDTO(
				task.getId(),
				task.getTitle(),
				task.getDescription(),
				task.getDueDate(),
				task.getStatus(),
				task.getPriority()
		);
	}
}
