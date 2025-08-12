package com.github.alefthallys.desafiotecnicopetize.utils;


import com.github.alefthallys.desafiotecnicopetize.dtos.SubTaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.SubTaskResponseDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskResponseDTO;
import com.github.alefthallys.desafiotecnicopetize.models.SubTask;
import com.github.alefthallys.desafiotecnicopetize.models.Task;

import java.util.stream.Collectors;

public class TaskMapperUtils {
	
	public static Task toEntity(TaskRequestDTO dto) {
		Task task = new Task();
		task.setTitle(dto.title());
		task.setDescription(dto.description());
		task.setDueDate(dto.dueDate());
		task.setStatus(dto.status());
		task.setPriority(dto.priority());
		
		if (dto.subTasks() != null) {
			dto.subTasks().forEach(subTaskDto -> {
				SubTask subTask = toSubTaskEntity(subTaskDto);
				subTask.setTask(task);
				task.getSubTasks().add(subTask);
			});
		}
		
		return task;
	}
	
	public static TaskResponseDTO toResponseDTO(Task task) {
		return new TaskResponseDTO(
				task.getId(),
				task.getTitle(),
				task.getDescription(),
				task.getDueDate(),
				task.getStatus(),
				task.getPriority(),
				task.getCreatedAt(),
				task.getUpdatedAt(),
				task.getSubTasks().stream().map(TaskMapperUtils::toResponseDTO).collect(Collectors.toList())
		);
	}
	
	public static SubTask toSubTaskEntity(SubTaskRequestDTO dto) {
		SubTask subTask = new SubTask();
		subTask.setTitle(dto.title());
		subTask.setStatus(dto.status());
		return subTask;
	}
	
	public static SubTaskResponseDTO toResponseDTO(SubTask subTask) {
		return new SubTaskResponseDTO(
				subTask.getId(),
				subTask.getTitle(),
				subTask.getStatus(),
				subTask.getCreatedAt(),
				subTask.getUpdatedAt()
		);
	}
}
