package com.github.alefthallys.desafiotecnicopetize.utils;


import com.github.alefthallys.desafiotecnicopetize.dtos.SubTaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.SubTaskResponseDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskResponseDTO;
import com.github.alefthallys.desafiotecnicopetize.models.SubTaskModel;
import com.github.alefthallys.desafiotecnicopetize.models.TaskModel;

import java.util.stream.Collectors;

public class TaskMapperUtils {
	
	public static TaskModel toEntity(TaskRequestDTO dto) {
		TaskModel taskModel = new TaskModel();
		taskModel.setTitle(dto.title());
		taskModel.setDescription(dto.description());
		taskModel.setDueDate(dto.dueDate());
		taskModel.setStatus(dto.status());
		taskModel.setPriority(dto.priority());
		
		if (dto.subTasks() != null) {
			dto.subTasks().forEach(subTaskDto -> {
				SubTaskModel subTaskModel = toSubTaskEntity(subTaskDto);
				subTaskModel.setTaskModel(taskModel);
				taskModel.getSubTaskModels().add(subTaskModel);
			});
		}
		
		return taskModel;
	}
	
	public static TaskResponseDTO toResponseDTO(TaskModel taskModel) {
		return new TaskResponseDTO(
				taskModel.getId(),
				taskModel.getTitle(),
				taskModel.getDescription(),
				taskModel.getDueDate(),
				taskModel.getStatus(),
				taskModel.getPriority(),
				taskModel.getCreatedAt(),
				taskModel.getUpdatedAt(),
				taskModel.getSubTaskModels().stream().map(TaskMapperUtils::toResponseDTO).collect(Collectors.toList())
		);
	}
	
	public static SubTaskModel toSubTaskEntity(SubTaskRequestDTO dto) {
		SubTaskModel subTaskModel = new SubTaskModel();
		subTaskModel.setTitle(dto.title());
		subTaskModel.setStatus(dto.status());
		return subTaskModel;
	}
	
	public static SubTaskResponseDTO toResponseDTO(SubTaskModel subTaskModel) {
		return new SubTaskResponseDTO(
				subTaskModel.getId(),
				subTaskModel.getTitle(),
				subTaskModel.getStatus(),
				subTaskModel.getCreatedAt(),
				subTaskModel.getUpdatedAt()
		);
	}
}
