package com.sandesdev.taskMaster.controllers;

import com.sandesdev.taskMaster.dtos.TaskDto;
import com.sandesdev.taskMaster.dtos.TaskItemDto;
import com.sandesdev.taskMaster.dtos.TaskResponse;
import com.sandesdev.taskMaster.dtos.TitleDto;
import com.sandesdev.taskMaster.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class TaskControllerTest {


    private static final UUID ID = UUID.randomUUID();
    private static final String TITLE = "teste";
    private static final String DESCRIPTION = "descrição do teste";
    private static final String STATUS = "progress";
    @InjectMocks
    private TaskController taskController;
    @Mock
    private TaskService taskService;
    @Mock
    JwtAuthenticationToken token;
    TaskItemDto taskItemDto;
    List<TaskResponse> tasks;
    TitleDto titleDto;
    TaskResponse taskResponse;
    Page<TaskResponse> taskPage;

    @BeforeEach
    void setUp() {
        openMocks(this);
        startContent();
    }

    private void startContent() {
        taskItemDto = new TaskItemDto(TITLE,DESCRIPTION,STATUS);
        tasks = List.of(new TaskResponse(ID,TITLE,DESCRIPTION,STATUS, Instant.now(),null));
        taskResponse = new TaskResponse(ID,TITLE,DESCRIPTION,STATUS, Instant.now(),null);
        taskPage = new PageImpl<>(tasks, PageRequest.of(0, 10), tasks.size());
    }

    @Test
    void wheCreateTaskThenReturnOK() {
        doNothing().when(taskService).save(taskItemDto,token);

        var response = taskController.createTask(taskItemDto,token);

        assertEquals(ResponseEntity.ok().build(), response);
        verify(taskService, times(1)).save(taskItemDto,token);
        //verifyNoInteractions(taskService.save(taskItemDto));
    }

    @Test
    void whenGetAllTaskThenReturnOK() {
        when(taskService.getAllTasks(0,10,token)).thenReturn(taskPage);

        var response = taskController.getAllTask(0,10,token);
        assertEquals(ResponseEntity.ok().build().getStatusCode(), response.getStatusCode());
        assertEquals(taskPage.getContent().get(0).title(), response.getBody().taskItens().get(0).title());
        verify(taskService, times(1)).getAllTasks(0,10,token);
       // verifyNoInteractions(taskService);
    }

    @Test
    void whenDeleteTaskThenReturnOK() {
        doNothing().when(taskService).delete(titleDto,token);

        var response = taskController.deleteTask(titleDto,token);

        assertEquals(ResponseEntity.ok().build(), response);
        verify(taskService, times(1)).delete(titleDto,token);
        //verifyNoInteractions(taskService);

    }

    @Test
    void whenPutTaskThenReturnOK() {
        doNothing().when(taskService).updateTask(ID,taskItemDto,token);

        var response = taskController.putTask(ID,taskItemDto,token);

        assertEquals(ResponseEntity.ok().build(), response);
        verify(taskService, times(1)).updateTask(ID,taskItemDto,token);
       // verifyNoInteractions(taskService);
    }
}