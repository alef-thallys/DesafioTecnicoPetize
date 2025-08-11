package com.github.alefthallys.desafiotecnicopetize.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskResponseDTO;
import com.github.alefthallys.desafiotecnicopetize.enums.Priority;
import com.github.alefthallys.desafiotecnicopetize.enums.Status;
import com.github.alefthallys.desafiotecnicopetize.services.TaskService;
import com.github.alefthallys.desafiotecnicopetize.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@DisplayName("TaskController Tests")
public class TaskControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private TaskService taskService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private TaskRequestDTO taskRequestDTO;
	private TaskResponseDTO taskResponseDTO;
	
	@BeforeAll
	static void configObjectMapper(@Autowired ObjectMapper mapper) {
		mapper.registerModule(new JavaTimeModule());
	}
	
	@BeforeEach
	public void setup() {
		taskRequestDTO = createRequest("title", "description", LocalDate.parse("2025-10-01"), Status.TODO, Priority.HIGH);
		taskResponseDTO = createResponse(1L, "title", "description", LocalDate.parse("2025-10-01"), Status.DONE, Priority.HIGH);
	}
	
	private TaskRequestDTO createRequest(String title, String desc, LocalDate date, Status status, Priority priority) {
		return new TaskRequestDTO(title, desc, date, status, priority);
	}
	
	private TaskResponseDTO createResponse(Long id, String title, String desc, LocalDate date, Status status, Priority priority) {
		return new TaskResponseDTO(id, title, desc, date, status, priority);
	}
	
	@Nested
	@DisplayName("Tests for findAllTasks")
	class FindAllTasksTests {
		@Test
		@DisplayName("Should return list of TaskResponseDTO when tasks exist")
		public void testFindAllTasksShouldReturnListOfTaskResponseDTO() throws Exception {
			when(taskService.findAll()).thenReturn(List.of(taskResponseDTO, taskResponseDTO));
			
			mockMvc.perform(get("/api/v1/tasks"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$").isArray())
					.andExpect(jsonPath("$").isNotEmpty())
					.andExpect(jsonPath("$[0].id").value(1L));
		}
		
		@Test
		@DisplayName("Should return empty list when no tasks exist")
		public void testFindAllTasksShouldReturnEmptyList() throws Exception {
			when(taskService.findAll()).thenReturn(List.of());
			
			mockMvc.perform(get("/api/v1/tasks"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$").isArray())
					.andExpect(jsonPath("$").isEmpty());
		}
	}
	
	@Nested
	@DisplayName("Tests for findTaskById")
	class FindTaskByIdTests {
		@Test
		@DisplayName("Should return TaskResponseDTO when ID exists")
		public void testFindTaskByIdShouldReturnTaskResponseDTO() throws Exception {
			when(taskService.findById(1L)).thenReturn(taskResponseDTO);
			
			mockMvc.perform(get("/api/v1/tasks/1"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(1L));
		}
		
		@Test
		@DisplayName("Should return Not Found when ID does not exist")
		public void testFindTaskByIdShouldReturnNotFound() throws Exception {
			when(taskService.findById(99L)).thenThrow(new ResourceNotFoundException("Task not found"));
			
			mockMvc.perform(get("/api/v1/tasks/99"))
					.andExpect(status().isNotFound());
		}
	}
	
	@Nested
	@DisplayName("Tests for createTask")
	class CreateTaskTests {
		@Test
		@DisplayName("Should return Created and TaskDTO on successful creation")
		public void testCreateTaskShouldReturnCreatedAndTaskDTO() throws Exception {
			when(taskService.create(any(TaskRequestDTO.class))).thenReturn(taskResponseDTO);
			
			mockMvc.perform(post("/api/v1/tasks")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(taskRequestDTO)))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.id").value(1L));
		}
		
		@Test
		@DisplayName("Should return Bad Request on validation error")
		public void testCreateTaskShouldReturnBadRequestOnValidationError() throws Exception {
			TaskRequestDTO invalidRequest = createRequest("", "", null, null, null);
			
			mockMvc.perform(post("/api/v1/tasks")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(invalidRequest)))
					.andExpect(status().isBadRequest());
		}
	}
	
	@Nested
	@DisplayName("Tests for updateTask")
	class UpdateTaskTests {
		@Test
		@DisplayName("Should return OK and updated TaskDTO on successful update")
		public void testUpdateTaskShouldReturnOkAndUpdatedTaskDTO() throws Exception {
			TaskResponseDTO updatedResponseDTO = createResponse(1L, "title", "description-updated", LocalDate.parse("2025-10-01"), Status.DONE, Priority.HIGH);
			
			when(taskService.update(eq(1L), any(TaskRequestDTO.class))).thenReturn(updatedResponseDTO);
			
			mockMvc.perform(put("/api/v1/tasks/1")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(taskRequestDTO)))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.description").value("description-updated"));
		}
		
		@Test
		@DisplayName("Should return Not Found when ID does not exist for update")
		public void testUpdateTaskShouldReturnNotFound() throws Exception {
			when(taskService.update(eq(99L), any(TaskRequestDTO.class))).thenThrow(new ResourceNotFoundException("Task not found"));
			
			mockMvc.perform(put("/api/v1/tasks/99")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(taskRequestDTO)))
					.andExpect(status().isNotFound());
		}
	}
	
	@Nested
	@DisplayName("Tests for deleteTask")
	class DeleteTaskTests {
		@Test
		@DisplayName("Should return No Content on successful deletion")
		public void testDeleteTaskShouldReturnNoContent() throws Exception {
			mockMvc.perform(delete("/api/v1/tasks/1"))
					.andExpect(status().isNoContent());
		}
		
		@Test
		@DisplayName("Should return Not Found when ID does not exist for deletion")
		public void testDeleteTaskShouldReturnNotFound() throws Exception {
			doThrow(new ResourceNotFoundException("Task not found")).when(taskService).delete(99L);
			
			mockMvc.perform(delete("/api/v1/tasks/99"))
					.andExpect(status().isNotFound());
		}
	}
}
