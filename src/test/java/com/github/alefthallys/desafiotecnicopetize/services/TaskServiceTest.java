package com.github.alefthallys.desafiotecnicopetize.services;

import com.github.alefthallys.desafiotecnicopetize.dtos.SubTaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskResponseDTO;
import com.github.alefthallys.desafiotecnicopetize.enums.Priority;
import com.github.alefthallys.desafiotecnicopetize.enums.Status;
import com.github.alefthallys.desafiotecnicopetize.exceptions.ResourceNotFoundException;
import com.github.alefthallys.desafiotecnicopetize.models.SubTask;
import com.github.alefthallys.desafiotecnicopetize.models.Task;
import com.github.alefthallys.desafiotecnicopetize.models.User;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Tests")
class TaskServiceTest {
	
	@Mock
	private TaskRepository taskRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private TaskService taskService;
	
	private User user;
	private Task task;
	private TaskRequestDTO taskRequestDTO;
	private UUID taskId;
	private UUID nonExistingTaskId;
	
	@BeforeEach
	void setUp() {
		taskId = UUID.randomUUID();
		nonExistingTaskId = UUID.randomUUID();
		
		user = new User();
		user.setId(UUID.randomUUID());
		user.setUsername("testuser");
		
		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("testuser");
		when(userRepository.findByUsername("testuser")).thenReturn(user);
		
		SubTask subTask = new SubTask();
		subTask.setId(UUID.randomUUID());
		subTask.setTitle("SubTask Title");
		subTask.setStatus(Status.TODO);
		
		task = new Task();
		task.setId(taskId);
		task.setTitle("title");
		task.setDescription("description");
		task.setDueDate(LocalDate.parse("2025-10-01"));
		task.setStatus(Status.TODO);
		task.setPriority(Priority.HIGH);
		task.setSubTasks(new ArrayList<>(Collections.singletonList(subTask)));
		task.setUser(user);
		
		SubTaskRequestDTO subTaskRequestDTO = new SubTaskRequestDTO("SubTask Title", Status.TODO);
		taskRequestDTO = new TaskRequestDTO("title", "description", LocalDate.parse("2025-10-01"), Status.TODO, Priority.HIGH, Collections.singletonList(subTaskRequestDTO));
	}
	
	// Helper to assert TaskResponseDTO fields
	private void assertTaskResponseEqualsTask(TaskResponseDTO dto, Task task) {
		assertEquals(task.getId(), dto.id());
		assertEquals(task.getTitle(), dto.title());
		assertEquals(task.getDescription(), dto.description());
		assertEquals(task.getDueDate(), dto.dueDate());
		assertEquals(task.getStatus(), dto.status());
		assertEquals(task.getPriority(), dto.priority());
		assertNotNull(dto.subTasks());
		assertEquals(task.getSubTasks().size(), dto.subTasks().size());
		for (int i = 0; i < task.getSubTasks().size(); i++) {
			assertEquals(task.getSubTasks().get(i).getTitle(), dto.subTasks().get(i).title());
			assertEquals(task.getSubTasks().get(i).getStatus(), dto.subTasks().get(i).status());
		}
	}
	
	@Nested
	@DisplayName("Tests for findAll")
	class FindAllTasksTests {
		@Test
		@DisplayName("Should return list of TaskResponseDTO when tasks exist")
		void testFindAllShouldReturnListOfTasks() {
			when(taskRepository.findByUserId(user.getId())).thenReturn(List.of(task));
			
			List<TaskResponseDTO> result = taskService.findAll();
			
			assertNotNull(result);
			assertEquals(1, result.size());
			assertTaskResponseEqualsTask(result.get(0), task);
			verify(taskRepository).findByUserId(user.getId());
		}
		
		@Test
		@DisplayName("Should return empty list when no tasks exist")
		void testFindAllShouldReturnEmptyList() {
			when(taskRepository.findByUserId(user.getId())).thenReturn(Collections.emptyList());
			
			List<TaskResponseDTO> result = taskService.findAll();
			
			assertNotNull(result);
			assertTrue(result.isEmpty());
			verify(taskRepository).findByUserId(user.getId());
		}
	}
	
	@Nested
	@DisplayName("Tests for findById")
	class FindTaskByIdTests {
		@Test
		@DisplayName("Should return TaskResponseDTO when ID exists")
		void testFindByIdShouldReturnTask() {
			when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
			
			TaskResponseDTO result = taskService.findById(taskId);
			
			assertNotNull(result);
			assertTaskResponseEqualsTask(result, task);
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
	class CreateTaskTests {
		@Test
		@DisplayName("Should create and return TaskResponseDTO")
		void testCreateShouldReturnCreatedTask() {
			when(taskRepository.save(any(Task.class))).thenReturn(task);
			
			TaskResponseDTO result = taskService.create(taskRequestDTO);
			
			assertNotNull(result);
			assertTaskResponseEqualsTask(result, task);
			verify(taskRepository).save(any(Task.class));
		}
		
		@Test
		@DisplayName("Should throw NullPointerException when TaskRequestDTO is null")
		void testCreateShouldThrowExceptionOnNull() {
			assertThrows(NullPointerException.class, () -> taskService.create(null));
		}
	}
	
	@Nested
	@DisplayName("Tests for update")
	class UpdateTaskTests {
		@Test
		@DisplayName("Should update and return updated TaskResponseDTO")
		void testUpdateShouldReturnUpdatedTask() {
			SubTaskRequestDTO updatedSubTaskRequest = new SubTaskRequestDTO("Updated SubTask", Status.DONE);
			TaskRequestDTO updatedRequest = new TaskRequestDTO("updated title", "updated desc", LocalDate.parse("2025-10-02"), Status.DONE, Priority.LOW, Collections.singletonList(updatedSubTaskRequest));
			
			SubTask updatedSubTask = new SubTask();
			updatedSubTask.setTitle("Updated SubTask");
			updatedSubTask.setStatus(Status.DONE);
			
			Task updatedTask = new Task();
			updatedTask.setId(taskId);
			updatedTask.setTitle("updated title");
			updatedTask.setDescription("updated desc");
			updatedTask.setDueDate(LocalDate.parse("2025-10-02"));
			updatedTask.setStatus(Status.DONE);
			updatedTask.setPriority(Priority.LOW);
			updatedTask.setSubTasks(new ArrayList<>(Collections.singletonList(updatedSubTask)));
			updatedTask.setUser(user);
			
			when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
			when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
			
			TaskResponseDTO result = taskService.update(taskId, updatedRequest);
			
			assertNotNull(result);
			assertTaskResponseEqualsTask(result, updatedTask);
			verify(taskRepository).findById(taskId);
			verify(taskRepository).save(any(Task.class));
		}
		
		@Test
		@DisplayName("Should throw ResourceNotFoundException when ID does not exist for update")
		void testUpdateShouldThrowException() {
			when(taskRepository.findById(nonExistingTaskId)).thenReturn(Optional.empty());
			
			assertThrows(ResourceNotFoundException.class, () -> taskService.update(nonExistingTaskId, taskRequestDTO));
			verify(taskRepository).findById(nonExistingTaskId);
			verify(taskRepository, never()).save(any(Task.class));
		}
	}
	
	@Nested
	@DisplayName("Tests for delete")
	class DeleteTaskTests {
		@Test
		@DisplayName("Should delete task successfully")
		void testDeleteShouldCompleteSuccessfully() {
			SubTask doneSubTask = new SubTask();
			doneSubTask.setId(UUID.randomUUID());
			doneSubTask.setTitle("SubTask DONE");
			doneSubTask.setStatus(Status.DONE);
			task.setSubTasks(new ArrayList<>(List.of(doneSubTask)));
			
			when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
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
			SubTask todoSubTask = new SubTask();
			todoSubTask.setId(UUID.randomUUID());
			todoSubTask.setTitle("SubTask TODO");
			todoSubTask.setStatus(Status.TODO);
			task.setSubTasks(new ArrayList<>(List.of(todoSubTask)));
			
			when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
			
			assertThrows(IllegalStateException.class, () -> taskService.delete(taskId));
			verify(taskRepository, never()).deleteById(any(UUID.class));
		}
	}
}
