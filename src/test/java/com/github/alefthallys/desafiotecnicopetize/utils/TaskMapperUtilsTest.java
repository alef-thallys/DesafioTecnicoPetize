package com.github.alefthallys.desafiotecnicopetize.utils;

import com.github.alefthallys.desafiotecnicopetize.dtos.SubTaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskRequestDTO;
import com.github.alefthallys.desafiotecnicopetize.enums.Priority;
import com.github.alefthallys.desafiotecnicopetize.enums.Status;
import com.github.alefthallys.desafiotecnicopetize.models.TaskModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TaskMapperUtils Tests")
class TaskMapperUtilsTest {

    @Test
    @DisplayName("toEntity maps basic fields and handles null subTasks")
    void testToEntityWithNullSubTasks() {
        TaskRequestDTO dto = new TaskRequestDTO("t", "d", LocalDate.parse("2025-10-01"), Status.TODO, Priority.MEDIUM, null);
        TaskModel model = TaskMapperUtils.toEntity(dto);
        assertEquals("t", model.getTitle());
        assertEquals("d", model.getDescription());
        assertEquals(LocalDate.parse("2025-10-01"), model.getDueDate());
        assertEquals(Status.TODO, model.getStatus());
        assertEquals(Priority.MEDIUM, model.getPriority());
        assertNotNull(model.getSubTaskModels());
        assertTrue(model.getSubTaskModels().isEmpty());
    }

    @Test
    @DisplayName("toEntity maps subTasks when present")
    void testToEntityWithSubTasks() {
        SubTaskRequestDTO sub = new SubTaskRequestDTO("s", Status.DONE);
        TaskRequestDTO dto = new TaskRequestDTO("t", "d", LocalDate.parse("2025-10-01"), Status.DONE, Priority.LOW, Collections.singletonList(sub));
        TaskModel model = TaskMapperUtils.toEntity(dto);
        assertEquals(1, model.getSubTaskModels().size());
        assertEquals("s", model.getSubTaskModels().get(0).getTitle());
        assertEquals(Status.DONE, model.getSubTaskModels().get(0).getStatus());
        assertSame(model, model.getSubTaskModels().get(0).getTaskModel());
    }

    @Test
    @DisplayName("toResponseDTO maps subTasks to responses")
    void testToResponseDTO() {
        SubTaskRequestDTO sub = new SubTaskRequestDTO("s", Status.TODO);
        TaskRequestDTO dto = new TaskRequestDTO("t", "d", LocalDate.parse("2025-10-01"), Status.TODO, Priority.HIGH, Collections.singletonList(sub));
        TaskModel model = TaskMapperUtils.toEntity(dto);
        var response = TaskMapperUtils.toResponseDTO(model);
        assertEquals("t", response.title());
        assertEquals(1, response.subTasks().size());
        assertEquals("s", response.subTasks().get(0).title());
        assertEquals(Status.TODO, response.subTasks().get(0).status());
    }
}

