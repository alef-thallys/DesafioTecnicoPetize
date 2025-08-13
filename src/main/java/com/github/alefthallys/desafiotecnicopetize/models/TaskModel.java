package com.github.alefthallys.desafiotecnicopetize.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.github.alefthallys.desafiotecnicopetize.enums.Priority;
import com.github.alefthallys.desafiotecnicopetize.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tasks")
@EqualsAndHashCode(of = "id")
public class TaskModel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
	private UUID id;
	
	@Column(name = "title", nullable = false, length = 255)
	private String title;
	
	@Column(name = "description", length = 1000)
	private String description;
	
	@Column(name = "due_date", nullable = false, columnDefinition = "DATE")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate dueDate;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "priority", nullable = false)
	private Priority priority;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private UserModel userModel;
	
	@OneToMany(mappedBy = "taskModel", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<SubTaskModel> subTaskModels = new ArrayList<>();
	
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
	
	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}
	
	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
