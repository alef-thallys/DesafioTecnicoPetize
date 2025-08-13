package com.github.alefthallys.desafiotecnicopetize.services;

import com.github.alefthallys.desafiotecnicopetize.dtos.SubTaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskResponseDTO;
import com.github.alefthallys.desafiotecnicopetize.enums.Priority;
import com.github.alefthallys.desafiotecnicopetize.enums.Status;
import com.github.alefthallys.desafiotecnicopetize.exceptions.ResourceNotFoundException;
import com.github.alefthallys.desafiotecnicopetize.models.SubTaskModel;
import com.github.alefthallys.desafiotecnicopetize.models.TaskModel;
import com.github.alefthallys.desafiotecnicopetize.models.UserModel;
import com.github.alefthallys.desafiotecnicopetize.repositories.TaskRepository;
import com.github.alefthallys.desafiotecnicopetize.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Tests")
class TaskModelServiceTest {
	
	@Mock
	private TaskRepository taskRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private TaskService taskService;
	
	private UserModel userModel;
	private TaskModel taskModel;
	private TaskRequestDTO taskRequestDTO;
	private UUID taskId;
	private UUID nonExistingTaskId;
	
	@BeforeEach
	void setUp() {
		taskId = UUID.randomUUID();
		nonExistingTaskId = UUID.randomUUID();
		
		userModel = new UserModel();
		userModel.setId(UUID.randomUUID());
		userModel.setUsername("testuser");
		
		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("testuser");
		when(userRepository.findByUsername("testuser")).thenReturn(userModel);
		
		SubTaskModel subTaskModel = new SubTaskModel();
		subTaskModel.setId(UUID.randomUUID());
		subTaskModel.setTitle("SubTask Title");
		subTaskModel.setStatus(Status.TODO);
		
		taskModel = new TaskModel();
		taskModel.setId(taskId);
		taskModel.setTitle("title");
		taskModel.setDescription("description");
		taskModel.setDueDate(LocalDate.parse("2025-10-01"));
		taskModel.setStatus(Status.TODO);
		taskModel.setPriority(Priority.HIGH);
		taskModel.setSubTaskModels(new ArrayList<>(Collections.singletonList(subTaskModel)));
		taskModel.setUserModel(userModel);
		
		SubTaskRequestDTO subTaskRequestDTO = new SubTaskRequestDTO("SubTask Title", Status.TODO);
		taskRequestDTO = new TaskRequestDTO("title", "description", LocalDate.parse("2025-10-01"), Status.TODO, Priority.HIGH, Collections.singletonList(subTaskRequestDTO));
	}
	
	// Helper to assert TaskResponseDTO fields
	private void assertTaskResponseEqualsTask(TaskResponseDTO dto, TaskModel taskModel) {
		assertEquals(taskModel.getId(), dto.id());
		assertEquals(taskModel.getTitle(), dto.title());
		assertEquals(taskModel.getDescription(), dto.description());
		assertEquals(taskModel.getDueDate(), dto.dueDate());
		assertEquals(taskModel.getStatus(), dto.status());
		assertEquals(taskModel.getPriority(), dto.priority());
		assertNotNull(dto.subTasks());
		assertEquals(taskModel.getSubTaskModels().size(), dto.subTasks().size());
		for (int i = 0; i < taskModel.getSubTaskModels().size(); i++) {
			assertEquals(taskModel.getSubTaskModels().get(i).getTitle(), dto.subTasks().get(i).title());
			assertEquals(taskModel.getSubTaskModels().get(i).getStatus(), dto.subTasks().get(i).status());
		}
	}
	
	@Nested
	@DisplayName("Tests for findAll")
	class FindAllTasksTests {
		@Test
		@DisplayName("Should return list of TaskResponseDTO when tasks exist")
		void testFindAllShouldReturnListOfTasks() {
			when(taskRepository.findByUserModelId(userModel.getId())).thenReturn(List.of(taskModel));
			
			List<TaskResponseDTO> result = taskService.findAll();
			
			assertNotNull(result);
			assertEquals(1, result.size());
			assertTaskResponseEqualsTask(result.get(0), taskModel);
			verify(taskRepository).findByUserModelId(userModel.getId());
		}
		
		@Test
		@DisplayName("Should return empty list when no tasks exist")
		void testFindAllShouldReturnEmptyList() {
			when(taskRepository.findByUserModelId(userModel.getId())).thenReturn(Collections.emptyList());
			
			List<TaskResponseDTO> result = taskService.findAll();
			
			assertNotNull(result);
			assertTrue(result.isEmpty());
			verify(taskRepository).findByUserModelId(userModel.getId());
		}
	}
	
	@Nested
	@DisplayName("Tests for findById")
	class FindTaskModelByIdTests {
		@Test
		@DisplayName("Should return TaskResponseDTO when ID exists")
		void testFindByIdShouldReturnTask() {
			when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskModel));
			
			TaskResponseDTO result = taskService.findById(taskId);
			
			assertNotNull(result);
			assertTaskResponseEqualsTask(result, taskModel);
			verify(taskRepository).findById(taskId);
		}
		
		@Test
		@DisplayName("Should throw ResourceNotFoundException when ID does not exist")
		void testFindByIdShouldThrowException() {
			when(taskRepository.findById(nonExistingTaskId)).thenReturn(Optional.empty());
			
			assertThrows(ResourceNotFoundException.class, () -> taskService.findById(nonExistingTaskId));
			verify(taskRepository).findById(nonExistingTaskId);
		}
	}
	
	@Nested
	@DisplayName("Tests for create")
	class CreateTaskModelTests {
		@Test
		@DisplayName("Should create and return TaskResponseDTO")
		void testCreateShouldReturnCreatedTask() {
			when(taskRepository.save(any(TaskModel.class))).thenReturn(taskModel);
			
			TaskResponseDTO result = taskService.create(taskRequestDTO);
			
			assertNotNull(result);
			assertTaskResponseEqualsTask(result, taskModel);
			verify(taskRepository).save(any(TaskModel.class));
		}
		
		@Test
		@DisplayName("Should throw NullPointerException when TaskRequestDTO is null")
		void testCreateShouldThrowExceptionOnNull() {
			assertThrows(NullPointerException.class, () -> taskService.create(null));
		}
	}
	
	@Nested
	@DisplayName("Tests for update")
	class UpdateTaskModelTests {
		@Test
		@DisplayName("Should update and return updated TaskResponseDTO")
		void testUpdateShouldReturnUpdatedTask() {
			SubTaskRequestDTO updatedSubTaskRequest = new SubTaskRequestDTO("Updated SubTask", Status.DONE);
			TaskRequestDTO updatedRequest = new TaskRequestDTO("updated title", "updated desc", LocalDate.parse("2025-10-02"), Status.DONE, Priority.LOW, Collections.singletonList(updatedSubTaskRequest));
			
			SubTaskModel updatedSubTaskModel = new SubTaskModel();
			updatedSubTaskModel.setTitle("Updated SubTask");
			updatedSubTaskModel.setStatus(Status.DONE);
			
			TaskModel updatedTaskModel = new TaskModel();
			updatedTaskModel.setId(taskId);
			updatedTaskModel.setTitle("updated title");
			updatedTaskModel.setDescription("updated desc");
			updatedTaskModel.setDueDate(LocalDate.parse("2025-10-02"));
			updatedTaskModel.setStatus(Status.DONE);
			updatedTaskModel.setPriority(Priority.LOW);
			updatedTaskModel.setSubTaskModels(new ArrayList<>(Collections.singletonList(updatedSubTaskModel)));
			updatedTaskModel.setUserModel(userModel);
			
			when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskModel));
			when(taskRepository.save(any(TaskModel.class))).thenReturn(updatedTaskModel);
			
			TaskResponseDTO result = taskService.update(taskId, updatedRequest);
			
			assertNotNull(result);
			assertTaskResponseEqualsTask(result, updatedTaskModel);
			verify(taskRepository).findById(taskId);
			verify(taskRepository).save(any(TaskModel.class));
		}
		
		@Test
		@DisplayName("Should throw ResourceNotFoundException when ID does not exist for update")
		void testUpdateShouldThrowException() {
			when(taskRepository.findById(nonExistingTaskId)).thenReturn(Optional.empty());
			
			assertThrows(ResourceNotFoundException.class, () -> taskService.update(nonExistingTaskId, taskRequestDTO));
			verify(taskRepository).findById(nonExistingTaskId);
			verify(taskRepository, never()).save(any(TaskModel.class));
		}
	}
	
	@Nested
	@DisplayName("Tests for delete")
	class DeleteTaskModelTests {
		@Test
		@DisplayName("Should delete task successfully")
		void testDeleteShouldCompleteSuccessfully() {
			SubTaskModel doneSubTaskModel = new SubTaskModel();
			doneSubTaskModel.setId(UUID.randomUUID());
			doneSubTaskModel.setTitle("SubTask DONE");
			doneSubTaskModel.setStatus(Status.DONE);
			taskModel.setSubTaskModels(new ArrayList<>(List.of(doneSubTaskModel)));
			
			when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskModel));
			doNothing().when(taskRepository).deleteById(taskId);
			
			assertDoesNotThrow(() -> taskService.delete(taskId));
			
			verify(taskRepository).findById(taskId);
			verify(taskRepository).deleteById(taskId);
		}
		
		@Test
		@DisplayName("Should throw ResourceNotFoundException when ID does not exist for deletion")
		void testDeleteShouldThrowException() {
			when(taskRepository.findById(nonExistingTaskId)).thenReturn(Optional.empty());
			
			assertThrows(ResourceNotFoundException.class, () -> taskService.delete(nonExistingTaskId));
			verify(taskRepository).findById(nonExistingTaskId);
			verify(taskRepository, never()).deleteById(any(UUID.class));
		}
		
		@Test
		@DisplayName("Should throw IllegalStateException when trying to delete Task with SubTasks in TODO status")
		void testDeleteShouldThrowExceptionIfHasTodoSubTask() {
			SubTaskModel todoSubTaskModel = new SubTaskModel();
			todoSubTaskModel.setId(UUID.randomUUID());
			todoSubTaskModel.setTitle("SubTask TODO");
			todoSubTaskModel.setStatus(Status.TODO);
			taskModel.setSubTaskModels(new ArrayList<>(List.of(todoSubTaskModel)));
			
			when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskModel));
			
			assertThrows(IllegalStateException.class, () -> taskService.delete(taskId));
			verify(taskRepository, never()).deleteById(any(UUID.class));
		}
	}
}
