package com.github.alefthallys.desafiotecnicopetize.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.alefthallys.desafiotecnicopetize.assemblers.TaskResponseAssembler;
import com.github.alefthallys.desafiotecnicopetize.dtos.SubTaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.SubTaskResponseDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskResponseDTO;
import com.github.alefthallys.desafiotecnicopetize.enums.Priority;
import com.github.alefthallys.desafiotecnicopetize.enums.Status;
import com.github.alefthallys.desafiotecnicopetize.exceptions.ResourceNotFoundException;
import com.github.alefthallys.desafiotecnicopetize.repositories.UserRepository;
import com.github.alefthallys.desafiotecnicopetize.security.TokenService;
import com.github.alefthallys.desafiotecnicopetize.services.AuthorizationService;
import com.github.alefthallys.desafiotecnicopetize.services.TaskService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import(TaskResponseAssembler.class)
@DisplayName("TaskController Tests")
@WithMockUser
public class TaskControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private TaskService taskService;

	@MockitoBean
	private TokenService tokenService;

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private AuthorizationService authorizationService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private TaskRequestDTO taskRequestDTO;
	private TaskResponseDTO taskResponseDTO;
	private UUID existingTaskId;
	private UUID nonExistingTaskId;
	
	@BeforeAll
	static void configObjectMapper(@Autowired ObjectMapper mapper) {
		mapper.registerModule(new JavaTimeModule());
	}
	
	@BeforeEach
	public void setup() {
		existingTaskId = UUID.randomUUID();
		nonExistingTaskId = UUID.randomUUID();
		
		SubTaskRequestDTO subTaskRequest = new SubTaskRequestDTO("SubTask Title", Status.TODO);
		taskRequestDTO = createRequest("title", "description", LocalDate.parse("2025-10-01"), Status.TODO, Priority.HIGH, Collections.singletonList(subTaskRequest));
		
		SubTaskResponseDTO subTaskResponse = new SubTaskResponseDTO(UUID.randomUUID(), "SubTask Title", Status.TODO, LocalDateTime.now(), LocalDateTime.now());
		taskResponseDTO = createResponse(existingTaskId, "title", "description", LocalDate.parse("2025-10-01"), Status.DONE, Priority.HIGH, Collections.singletonList(subTaskResponse));
	}
	
	private TaskRequestDTO createRequest(String title, String desc, LocalDate date, Status status, Priority priority, List<SubTaskRequestDTO> subTasks) {
		return new TaskRequestDTO(title, desc, date, status, priority, subTasks);
	}
	
	private TaskResponseDTO createResponse(UUID id, String title, String desc, LocalDate date, Status status, Priority priority, List<SubTaskResponseDTO> subTasks) {
		LocalDateTime now = LocalDateTime.now();
		return new TaskResponseDTO(id, title, desc, date, status, priority, now, now, subTasks);
	}
	
	// Helper for JSON content type
	private String toJson(Object obj) throws Exception {
		return objectMapper.writeValueAsString(obj);
	}
	
	@Nested
	@DisplayName("Tests for findAll")
	class FindAllTasksTests {
		@Test
		@DisplayName("Should return list of TaskResponseDTO when tasks exist")
		public void testFindAllTasksShouldReturnListOfTaskResponseDTO() throws Exception {
			when(taskService.findAll()).thenReturn(List.of(taskResponseDTO, taskResponseDTO));
			
			mockMvc.perform(get("/api/v1/tasks"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$._embedded.taskResponseDTOList").isArray())
					.andExpect(jsonPath("$._embedded.taskResponseDTOList").isNotEmpty())
					.andExpect(jsonPath("$._embedded.taskResponseDTOList[0].id").value(existingTaskId.toString()))
					.andExpect(jsonPath("$._embedded.taskResponseDTOList[0].subTasks[0].title").value("SubTask Title"))
					.andExpect(content().contentType("application/json"));
		}
		
		@Test
		@DisplayName("Should return empty list when no tasks exist")
		public void testFindAllTasksShouldReturnEmptyList() throws Exception {
			when(taskService.findAll()).thenReturn(List.of());
			
			mockMvc.perform(get("/api/v1/tasks"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$._embedded").doesNotExist())
					.andExpect(content().contentType("application/json"));
		}
	}
	
	@Nested
	@DisplayName("Tests for findById")
	class FindTaskByIdTests {
		@Test
		@DisplayName("Should return TaskResponseDTO when ID exists")
		public void testFindTaskByIdShouldReturnTaskResponseDTO() throws Exception {
			when(taskService.findById(existingTaskId)).thenReturn(taskResponseDTO);
			
			mockMvc.perform(get("/api/v1/tasks/{id}", existingTaskId))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(existingTaskId.toString()))
					.andExpect(jsonPath("$.subTasks[0].title").value("SubTask Title"))
					.andExpect(content().contentType("application/json"));
		}
		
		@Test
		@DisplayName("Should return Not Found when ID does not exist")
		public void testFindTaskByIdShouldReturnNotFound() throws Exception {
			when(taskService.findById(nonExistingTaskId)).thenThrow(new ResourceNotFoundException("Task not found"));
			
			mockMvc.perform(get("/api/v1/tasks/{id}", nonExistingTaskId))
					.andExpect(status().isNotFound());
		}
	}
	
	@Nested
	@DisplayName("Tests for create")
	class CreateTaskTests {
		@Test
		@DisplayName("Should return Created and TaskDTO on successful creation")
		public void testCreateTaskShouldReturnCreatedAndTaskDTO() throws Exception {
			when(taskService.create(any(TaskRequestDTO.class))).thenReturn(taskResponseDTO);
			
			mockMvc.perform(post("/api/v1/tasks")
							.with(csrf())
							.contentType(MediaType.APPLICATION_JSON)
							.content(toJson(taskRequestDTO)))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.id").value(existingTaskId.toString()))
					.andExpect(jsonPath("$.subTasks[0].title").value("SubTask Title"))
					.andExpect(content().contentType("application/json"));
		}
		
		@Test
		@DisplayName("Should return Bad Request on validation error")
		public void testCreateTaskShouldReturnBadRequestOnValidationError() throws Exception {
			TaskRequestDTO invalidRequest = createRequest("", "", null, null, null, null);
			
			mockMvc.perform(post("/api/v1/tasks")
							.with(csrf())
							.contentType(MediaType.APPLICATION_JSON)
							.content(toJson(invalidRequest)))
					.andExpect(status().isBadRequest());
		}
	}
	
	@Nested
	@DisplayName("Tests for update")
	class UpdateTaskTests {
		@Test
		@DisplayName("Should return OK and updated TaskDTO on successful update")
		public void testUpdateTaskShouldReturnOkAndUpdatedTaskDTO() throws Exception {
			SubTaskResponseDTO updatedSubTaskResponse = new SubTaskResponseDTO(UUID.randomUUID(), "Updated SubTask", Status.DONE, LocalDateTime.now(), LocalDateTime.now());
			TaskResponseDTO updatedResponseDTO = createResponse(existingTaskId, "title", "description-updated", LocalDate.parse("2025-10-01"), Status.DONE, Priority.HIGH, Collections.singletonList(updatedSubTaskResponse));
			
			when(taskService.update(eq(existingTaskId), any(TaskRequestDTO.class))).thenReturn(updatedResponseDTO);
			
			mockMvc.perform(put("/api/v1/tasks/{id}", existingTaskId)
							.with(csrf())
							.contentType(MediaType.APPLICATION_JSON)
							.content(toJson(taskRequestDTO)))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.description").value("description-updated"))
					.andExpect(jsonPath("$.subTasks[0].title").value("Updated SubTask"))
					.andExpect(content().contentType("application/json"));
		}
		
		@Test
		@DisplayName("Should return Not Found when ID does not exist for update")
		public void testUpdateTaskShouldReturnNotFound() throws Exception {
			when(taskService.update(eq(nonExistingTaskId), any(TaskRequestDTO.class))).thenThrow(new ResourceNotFoundException("Task not found"));
			
			mockMvc.perform(put("/api/v1/tasks/{id}", nonExistingTaskId)
							.with(csrf())
							.contentType(MediaType.APPLICATION_JSON)
							.content(toJson(taskRequestDTO)))
					.andExpect(status().isNotFound());
		}
	}
	
	@Nested
	@DisplayName("Tests for delete")
	class DeleteTaskTests {
		@Test
		@DisplayName("Should return No Content on successful deletion")
		public void testDeleteTaskShouldReturnNoContent() throws Exception {
			mockMvc.perform(delete("/api/v1/tasks/{id}", existingTaskId).with(csrf()))
					.andExpect(status().isNoContent());
		}
		
		@Test
		@DisplayName("Should return Not Found when ID does not exist for deletion")
		public void testDeleteTaskShouldReturnNotFound() throws Exception {
			doThrow(new ResourceNotFoundException("Task not found")).when(taskService).delete(nonExistingTaskId);
			
			mockMvc.perform(delete("/api/v1/tasks/{id}", nonExistingTaskId).with(csrf()))
					.andExpect(status().isNotFound());
		}
	}
}
