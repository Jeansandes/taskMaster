package com.sandesdev.taskMaster.controllers;

import com.sandesdev.taskMaster.dtos.TaskDto;
import com.sandesdev.taskMaster.dtos.TaskItemDto;
import com.sandesdev.taskMaster.dtos.TitleDto;
import com.sandesdev.taskMaster.services.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@ResponseBody
@RequestMapping("/task")
public class TaskController {
    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Void> createTask(@RequestBody TaskItemDto taskDto,JwtAuthenticationToken token){
        taskService.save(taskDto, token);
        return ResponseEntity.ok().build();
    }
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDto> getAllTask(@RequestParam(value = "page", defaultValue = "0") int page,
                                              @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,JwtAuthenticationToken token){
        var tasks = taskService.getAllTasks(page, pageSize,token);
        return ResponseEntity.ok(new TaskDto(tasks.getContent(), page, pageSize, tasks.getTotalPages(), tasks.getTotalElements()));
    }
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteTask(@RequestBody TitleDto title,
                                           JwtAuthenticationToken token){
        taskService.delete(title, token);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> putTask(@PathVariable UUID id, @RequestBody TaskItemDto taskDto,JwtAuthenticationToken token){
        taskService.updateTask(id,taskDto, token);
        return ResponseEntity.ok().build();
    }
}
