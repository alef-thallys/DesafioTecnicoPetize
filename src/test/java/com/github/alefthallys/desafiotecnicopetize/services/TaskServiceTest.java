package com.github.alefthallys.desafiotecnicopetize.services;

import com.github.alefthallys.desafiotecnicopetize.dtos.TaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskResponseDTO;
import com.github.alefthallys.desafiotecnicopetize.enums.Priority;
import com.github.alefthallys.desafiotecnicopetize.enums.Status;
import com.github.alefthallys.desafiotecnicopetize.exceptions.ResourceNotFoundException;
import com.github.alefthallys.desafiotecnicopetize.models.Task;
import com.github.alefthallys.desafiotecnicopetize.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskRequestDTO taskRequestDTO;
    private UUID taskId;
    private UUID nonExistingTaskId;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        nonExistingTaskId = UUID.randomUUID();
        task = new Task(taskId, "title", "description", LocalDate.parse("2025-10-01"), Status.TODO, Priority.HIGH, null, null);
        taskRequestDTO = new TaskRequestDTO("title", "description", LocalDate.parse("2025-10-01"), Status.TODO, Priority.HIGH);
    }

    @Nested
    @DisplayName("Tests for findAll")
    class FindAllTasksTests {
        @Test
        @DisplayName("Should return list of TaskResponseDTO when tasks exist")
        void testFindAllShouldReturnListOfTasks() {
            when(taskRepository.findAll()).thenReturn(List.of(task));

            List<TaskResponseDTO> result = taskService.findAll();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(task.getId(), result.get(0).id());
            verify(taskRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no tasks exist")
        void testFindAllShouldReturnEmptyList() {
            when(taskRepository.findAll()).thenReturn(Collections.emptyList());

            List<TaskResponseDTO> result = taskService.findAll();

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(taskRepository).findAll();
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
            assertEquals(task.getId(), result.id());
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
            assertEquals(task.getId(), result.id());
            verify(taskRepository).save(any(Task.class));
        }
    }

    @Nested
    @DisplayName("Tests for update")
    class UpdateTaskTests {
        @Test
        @DisplayName("Should update and return updated TaskResponseDTO")
        void testUpdateShouldReturnUpdatedTask() {
            TaskRequestDTO updatedRequest = new TaskRequestDTO("updated title", "updated desc", LocalDate.parse("2025-10-02"), Status.DONE, Priority.LOW);
            Task updatedTask = new Task(taskId, "updated title", "updated desc", LocalDate.parse("2025-10-02"), Status.DONE, Priority.LOW, null, null);

            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

            TaskResponseDTO result = taskService.update(taskId, updatedRequest);

            assertNotNull(result);
            assertEquals("updated title", result.title());
            assertEquals(Status.DONE, result.status());
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
            when(taskRepository.existsById(taskId)).thenReturn(true);
            doNothing().when(taskRepository).deleteById(taskId);

            assertDoesNotThrow(() -> taskService.delete(taskId));

            verify(taskRepository).existsById(taskId);
            verify(taskRepository).deleteById(taskId);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when ID does not exist for deletion")
        void testDeleteShouldThrowException() {
            when(taskRepository.existsById(nonExistingTaskId)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class, () -> taskService.delete(nonExistingTaskId));

            verify(taskRepository).existsById(nonExistingTaskId);
            verify(taskRepository, never()).deleteById(any(UUID.class));
        }
    }
}