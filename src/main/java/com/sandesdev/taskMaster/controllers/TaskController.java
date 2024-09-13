package com.sandesdev.taskMaster.controllers;

import com.sandesdev.taskMaster.dtos.TaskDto;
import com.sandesdev.taskMaster.dtos.TaskItemDto;
import com.sandesdev.taskMaster.dtos.TitleDto;
import com.sandesdev.taskMaster.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@ResponseBody
@RequestMapping(value = "/task",produces = {"aplication/json"})
@Tag(name = "task-master")
public class TaskController {
    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "cria uma nova tarefa", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "tarefa criada com sucesso"),
            @ApiResponse(responseCode = "401", description = "falha na autenticação")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createTask(@RequestBody TaskItemDto taskDto,JwtAuthenticationToken token){
        taskService.save(taskDto, token);
        return ResponseEntity.ok().build();
    }
    @Operation(summary = "busca todas as terefas do usuário", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "tarefa criada com sucesso"),
            @ApiResponse(responseCode = "401", description = "falha na autenticação")
    })
    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDto> getAllTask(@RequestParam(value = "page", defaultValue = "0") int page,
                                              @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,JwtAuthenticationToken token){
        var tasks = taskService.getAllTasks(page, pageSize,token);
        return ResponseEntity.ok(new TaskDto(tasks.getContent(), page, pageSize, tasks.getTotalPages(), tasks.getTotalElements()));
    }
    @Operation(summary = "Deleta uma tarefa", method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "tarefa criada com sucesso"),
            @ApiResponse(responseCode = "401", description = "falha na autenticação")
    })
    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteTask(@RequestBody TitleDto title,
                                           JwtAuthenticationToken token){
        taskService.delete(title, token);
        return ResponseEntity.ok().build();
    }
    @Operation(summary = "Atualiza uma tarefa", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "tarefa criada com sucesso"),
            @ApiResponse(responseCode = "401", description = "falha na autenticação")
    })
    @PutMapping(value = "/{id}",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> putTask(@PathVariable UUID id, @RequestBody TaskItemDto taskDto,JwtAuthenticationToken token){
        taskService.updateTask(id,taskDto, token);
        return ResponseEntity.ok().build();
    }
}
