package com.github.alefthallys.desafiotecnicopetize.repositories;

import com.github.alefthallys.desafiotecnicopetize.models.TaskModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, UUID> {
	List<TaskModel> findByUserModelId(UUID id);
}
