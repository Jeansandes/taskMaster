package com.sandesdev.taskMaster.services;

import com.sandesdev.taskMaster.dtos.TaskItemDto;
import com.sandesdev.taskMaster.exceptions.ForbiddenException;
import com.sandesdev.taskMaster.exceptions.IdNotFoundException;
import com.sandesdev.taskMaster.models.Role;
import com.sandesdev.taskMaster.models.Status;
import com.sandesdev.taskMaster.models.TaskModel;
import com.sandesdev.taskMaster.repositories.TaskRepository;
import com.sandesdev.taskMaster.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class TaskService {
    private TaskRepository taskRepository;
    private UserServices userServices;
    private UserRepository userRepository;

    public void save(TaskItemDto taskDto, JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName()));
        var taskModel = new TaskModel();
        taskModel.setTitle(taskDto.title());
        taskModel.setDescription(taskDto.description());
        taskModel.setCriation(Instant.now());
        taskModel.setStatus(Status.PENDING);
        taskModel.setUser(user.get());
        taskRepository.save(taskModel);

    }

    public Page<TaskItemDto> getAllTasks(int page, int pageSize) {
        var tasks = taskRepository.findAll(
                        PageRequest.of(page, pageSize, Sort.Direction.DESC, "creationTimestamp"))
                .map(task ->
                        new TaskItemDto(
                                task.getTitle(),
                                task.getDescription())
                );
                return tasks;
    }

    public void delete(UUID id, JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName()));
        var task = taskRepository.findById(id).orElseThrow( ()-> new IdNotFoundException("id not found!"));

        var isAdmin = user.get().getRoles()
                        .stream().anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if(isAdmin || task.getUser().getUserId().equals(UUID.fromString(token.getName()))){
            taskRepository.delete(task);
        } else {
            throw new ForbiddenException("you don't have permission to access this resource");
        }
    }
}
