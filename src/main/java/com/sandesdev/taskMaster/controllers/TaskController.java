package com.sandesdev.taskMaster.controllers;

import com.sandesdev.taskMaster.dtos.TaskDto;
import com.sandesdev.taskMaster.dtos.TaskItemDto;
import com.sandesdev.taskMaster.services.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@ResponseBody
@RequestMapping("/task")
public class TaskController {
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<Void> createTask(@RequestBody TaskItemDto taskDto){
        taskService.save(taskDto);
        return ResponseEntity.ok().build();
    }
    @GetMapping
    public ResponseEntity<TaskDto> getAllTask(@RequestParam(value = "page", defaultValue = "0") int page,
                                              @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        var tasks = taskService.getAllTasks(page, pageSize);
        return ResponseEntity.ok(new TaskDto(tasks.getContent(), page, pageSize, tasks.getTotalPages(), tasks.getTotalElements()));
    }
    @DeleteMapping
    public ResponseEntity<Void> deleteTask(@PathVariable Long id,
                                           JwtAuthenticationToken token){
        return ResponseEntity.ok().build();
    }
    @PutMapping
    public ResponseEntity<Void> putTask(){
        return ResponseEntity.ok().build();
    }
}
