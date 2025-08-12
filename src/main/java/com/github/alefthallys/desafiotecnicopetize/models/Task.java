package com.github.alefthallys.desafiotecnicopetize.models;

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
public class Task {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
	private UUID id;
	
	@Column(name = "title", nullable = false)
	private String title;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "due_date", nullable = false)
	private LocalDate dueDate;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "priority", nullable = false)
	private Priority priority;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<SubTask> subTasks = new ArrayList<>();
	
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
