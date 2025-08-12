package com.github.alefthallys.desafiotecnicopetize.assemblers;

import com.github.alefthallys.desafiotecnicopetize.controllers.TaskController;
import com.github.alefthallys.desafiotecnicopetize.dtos.TaskResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TaskResponseAssembler implements RepresentationModelAssembler<TaskResponseDTO, EntityModel<TaskResponseDTO>> {
    @Override
    public EntityModel<TaskResponseDTO> toModel(TaskResponseDTO task) {
        UUID id = task.id();
        EntityModel<TaskResponseDTO> resource = EntityModel.of(task);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TaskController.class).findById(id)).withSelfRel());
//        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TaskController.class).update(id, null)).withRel("update"));
//        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TaskController.class).delete(id)).withRel("delete"));
        return resource;
    }
}

