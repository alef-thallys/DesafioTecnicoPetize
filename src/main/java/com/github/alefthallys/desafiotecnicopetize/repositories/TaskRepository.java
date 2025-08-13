package com.github.alefthallys.desafiotecnicopetize.repositories;

import com.github.alefthallys.desafiotecnicopetize.models.TaskModel;
import com.github.alefthallys.desafiotecnicopetize.enums.Status;
import com.github.alefthallys.desafiotecnicopetize.enums.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, UUID> {
	List<TaskModel> findByUserModelId(UUID id);

	@Query("SELECT t FROM TaskModel t WHERE t.userModel.id = :userId"
		+ " AND (:status IS NULL OR t.status = :status)"
		+ " AND (:priority IS NULL OR t.priority = :priority)"
		+ " AND (:dueDate IS NULL OR t.dueDate = :dueDate)")
	List<TaskModel> findByFilters(@Param("userId") UUID userId,
			@Param("status") Status status,
			@Param("priority") Priority priority,
			@Param("dueDate") LocalDate dueDate);
}
