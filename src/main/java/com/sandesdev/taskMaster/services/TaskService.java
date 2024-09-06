package com.sandesdev.taskMaster.services;

import com.sandesdev.taskMaster.dtos.TaskItemDto;
import com.sandesdev.taskMaster.dtos.TaskResponse;
import com.sandesdev.taskMaster.dtos.TitleDto;
import com.sandesdev.taskMaster.exceptions.ForbiddenException;
import com.sandesdev.taskMaster.exceptions.TaskNotFoundException;
import com.sandesdev.taskMaster.models.Role;
import com.sandesdev.taskMaster.models.TaskModel;
import com.sandesdev.taskMaster.repositories.StatusRepository;
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
    private StatusRepository statusRepository;

    public TaskService(TaskRepository taskRepository, UserServices userServices, UserRepository userRepository, StatusRepository statusRepository) {
        this.taskRepository = taskRepository;
        this.userServices = userServices;
        this.userRepository = userRepository;
        this.statusRepository = statusRepository;
    }

    public void save(TaskItemDto taskDto, JwtAuthenticationToken token) {
        var status = statusRepository.findByName(taskDto.status());
        var user = userRepository.findById(UUID.fromString(token.getName()));
        var taskModel = new TaskModel();
        taskModel.setTitle(taskDto.title());
        taskModel.setDescription(taskDto.description());
        taskModel.setCriation(Instant.now());
        taskModel.setStatus(status);
        taskModel.setUser(user.get());
        taskRepository.save(taskModel);

    }

    public Page<TaskResponse> getAllTasks(int page, int pageSize,JwtAuthenticationToken token) {
            var userId = UUID.fromString(token.getName());
            var user = userRepository.findById(userId);
            var tasks = taskRepository.findAllByUser(user.get(),PageRequest.of(page, pageSize, Sort.Direction.DESC, "criation"))
                    .map(task ->
                            new TaskResponse(
                                    task.getTaskId(),
                                    task.getTitle(),
                                    task.getDescription(),
                                    task.getStatus().getName(),
                                    task.getCriation(),
                                    task.getConclusion()
                            )
                    );
            return tasks;
    }

    public void delete(TitleDto titleDto, JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName()));
        var task = taskRepository.findByTitle(titleDto.title()).orElseThrow(() -> new TaskNotFoundException("Title not found!"));

        var isAdmin = user.get().getRoles()
                        .stream().anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if(isAdmin || task.getUser().getUserId().equals(UUID.fromString(token.getName()))){
            taskRepository.delete(task);
        } else {
            throw new ForbiddenException("you don't have permission to access this resource");
        }
    }

    public void updateTask(UUID id, TaskItemDto taskDto, JwtAuthenticationToken token) {
        var status = statusRepository.findByName(taskDto.status());
        var user = userRepository.findById(UUID.fromString(token.getName()));
        var taskModel = new TaskModel();
        taskModel.setTaskId(id);
        taskModel.setTitle(taskDto.title());
        taskModel.setDescription(taskDto.description());
        taskModel.setCriation(Instant.now());
        taskModel.setUser(user.get());
        taskModel.setStatus(status);
        if(status.getStatusId() == 3L){
            taskModel.setConclusion(Instant.now());
        }

        taskRepository.save(taskModel);
    }
}
