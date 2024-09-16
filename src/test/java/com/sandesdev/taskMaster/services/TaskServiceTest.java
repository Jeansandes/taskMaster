package com.sandesdev.taskMaster.services;

import com.sandesdev.taskMaster.dtos.TaskItemDto;
import com.sandesdev.taskMaster.dtos.TaskResponse;
import com.sandesdev.taskMaster.dtos.TitleDto;
import com.sandesdev.taskMaster.exceptions.ForbiddenException;
import com.sandesdev.taskMaster.exceptions.TaskNotFoundException;
import com.sandesdev.taskMaster.models.Role;
import com.sandesdev.taskMaster.models.Status;
import com.sandesdev.taskMaster.models.TaskModel;
import com.sandesdev.taskMaster.models.UserModel;
import com.sandesdev.taskMaster.repositories.StatusRepository;
import com.sandesdev.taskMaster.repositories.TaskRepository;
import com.sandesdev.taskMaster.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class TaskServiceTest {
    private final static UUID ID = UUID.randomUUID();
    private final static String NAME = "jean";
    private final static String EMAIL = "jean@gmail.com";
    private final static String PASSWORD = "123";
    private final static String TITLE = "123";
    private final static String DESCRIPITION = "123";
    private final static Instant CRIATION = Instant.now();
    @InjectMocks
    private TaskService taskService;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StatusRepository statusRepository;
    @Mock
    private JwtAuthenticationToken token;
    UserModel userModel;
    TaskModel taskModel;
    Status status ;
    TaskItemDto taskItemDto;
    Page<TaskModel> taskPage;
    @BeforeEach
    void setUp() {
        openMocks(this);
        startContent();
    }

    private void startContent() {
        status = new Status(1L, "pending");
        userModel = new UserModel(ID,NAME,EMAIL,PASSWORD);
        taskModel = new TaskModel(ID,TITLE,DESCRIPITION,CRIATION,null,status,userModel);
        taskItemDto = new TaskItemDto(TITLE,DESCRIPITION,status.getName());
        taskPage = new PageImpl<>(List.of(taskModel));
    }

    @Test
    void whenSaveThenReturnSuccess() {
        when(statusRepository.findByName(any())).thenReturn(status);
        when(token.getName()).thenReturn(ID.toString());
        when(userRepository.findById(any())).thenReturn(Optional.of(userModel));

        taskService.save(taskItemDto,token);
        verify(taskRepository,times(1)).save(argThat(taskModel -> taskModel.getTitle().equals(TITLE)));
        verify(taskRepository,times(1)).save(argThat(taskModel -> taskModel.getDescription().equals(DESCRIPITION)));
    }

    @Test
    void whenGetAllTasksThenReturnPageTask() {
        // Mocka o token retornando o ID (UUID) como String
        when(token.getName()).thenReturn(ID.toString());

        // Mocka a busca de usuário pelo UUID retornado
        when(userRepository.findById(ID)).thenReturn(Optional.of(userModel));

        // Mocka a páginação de tarefas retornando uma lista de TaskModel
        when(taskRepository.findAllByUser(eq(userModel), any(PageRequest.class))).thenReturn(taskPage);

        // Executa o método a ser testado
        Page<TaskResponse> result = taskService.getAllTasks(0, 10, token);

        // Verifica o comportamento esperado
        assertEquals(1, result.getTotalElements());
        TaskResponse response = result.getContent().get(0);
        assertEquals(taskModel.getTaskId(), response.id());
        assertEquals(taskModel.getTitle(), response.title());
        assertEquals(taskModel.getDescription(), response.description());
        assertEquals(taskModel.getStatus().getName(), response.status());
        assertEquals(taskModel.getCriation(), response.criation());
        assertEquals(taskModel.getConclusion(), response.conclusion());

        // Verifica se o repositório foi chamado corretamente
        verify(taskRepository, times(1)).findAllByUser(eq(userModel), any(PageRequest.class));
    }

    @Test
    void whenDeleteWithAdminUserThenSuccess() {
        // Mocka o token retornando o ID (UUID) como String
        when(token.getName()).thenReturn(ID.toString());

        // Mocka o usuário retornando um usuário com papel de administrador
        Role adminRole = new Role(1L, Role.Values.ADMIN.name());
        userModel.setRoles(Set.of(adminRole));
        when(userRepository.findById(ID)).thenReturn(Optional.of(userModel));

        // Mocka a busca da tarefa pelo título
        when(taskRepository.findByTitle(TITLE)).thenReturn(Optional.of(taskModel));

        // Executa o método a ser testado
        taskService.delete(new TitleDto(TITLE), token);

        // Verifica se o taskRepository.delete() foi chamado corretamente
        verify(taskRepository, times(1)).delete(taskModel);
    }

    @Test
    void whenDeleteWithNonAdminAndOwnerUserThenSuccess() {
        // Mocka o token retornando o ID (UUID) como String
        when(token.getName()).thenReturn(ID.toString());

        // Mocka o usuário retornando um usuário comum (não administrador) mas que é o dono da tarefa
        userModel.setRoles(Set.of(new Role(2L, Role.Values.BASIC.name())));
        when(userRepository.findById(ID)).thenReturn(Optional.of(userModel));

        // Mocka a tarefa retornada com o mesmo usuário
        taskModel.setUser(userModel);
        when(taskRepository.findByTitle(TITLE)).thenReturn(Optional.of(taskModel));

        // Executa o método a ser testado
        taskService.delete(new TitleDto(TITLE), token);

        // Verifica se o taskRepository.delete() foi chamado corretamente
        verify(taskRepository, times(1)).delete(taskModel);
    }

    @Test
    void whenDeleteWithNonOwnerUserThenThrowForbiddenException() {
        // Mocka o token retornando o ID (UUID) como String
        when(token.getName()).thenReturn(ID.toString());

        // Mocka o usuário retornando um usuário comum (não administrador)
        userModel.setRoles(Set.of(new Role(2L, Role.Values.BASIC.name())));
        when(userRepository.findById(ID)).thenReturn(Optional.of(userModel));

        // Mocka a tarefa retornada com um usuário diferente
        UserModel otherUser = new UserModel(UUID.randomUUID(), "otherUser", "otherEmail", "otherPassword");
        taskModel.setUser(otherUser);
        when(taskRepository.findByTitle(TITLE)).thenReturn(Optional.of(taskModel));

        // Executa o método a ser testado e espera a exceção
        assertThrows(ForbiddenException.class, () -> taskService.delete(new TitleDto(TITLE), token));

        // Verifica que o repositório não foi chamado para deletar
        verify(taskRepository, never()).delete(any());
    }

    @Test
    void whenDeleteWithNonExistingTaskThenThrowTaskNotFoundException() {
        // Mocka o token retornando o ID (UUID) como String
        when(token.getName()).thenReturn(ID.toString());

        // Mocka o usuário retornado
        when(userRepository.findById(ID)).thenReturn(Optional.of(userModel));

        // Mocka o repositório para lançar a exceção quando a tarefa não for encontrada
        when(taskRepository.findByTitle(TITLE)).thenReturn(Optional.empty());

        // Executa o método a ser testado e espera a exceção
        assertThrows(TaskNotFoundException.class, () -> taskService.delete(new TitleDto(TITLE), token));

        // Verifica que o repositório não foi chamado para deletar
        verify(taskRepository, never()).delete(any());
    }
    @Test
    void whenUpdateTaskThenSuccess() {
        // Mocka o token retornando o ID (UUID) como String
        when(token.getName()).thenReturn(ID.toString());

        // Mocka a busca de status pelo nome
        when(statusRepository.findByName(taskItemDto.status())).thenReturn(status);

        // Mocka a busca de usuário pelo UUID retornado
        when(userRepository.findById(ID)).thenReturn(Optional.of(userModel));

        // Cria o UUID da tarefa para atualização
        UUID taskId = UUID.randomUUID();

        // Executa o método a ser testado
        taskService.updateTask(taskId, taskItemDto, token);

        // Verifica se o taskRepository.save() foi chamado com as informações corretas
        verify(taskRepository, times(1)).save(argThat(taskModel ->
                taskModel.getTaskId().equals(taskId) &&
                        taskModel.getTitle().equals(TITLE) &&
                        taskModel.getDescription().equals(DESCRIPITION) &&
                        taskModel.getCriation() != null &&
                        taskModel.getUser().equals(userModel) &&
                        taskModel.getStatus().equals(status)
        ));
    }

    @Test
    void whenUpdateTaskAndStatusIsCompletedThenSetConclusion() {
        // Mocka o token retornando o ID (UUID) como String
        when(token.getName()).thenReturn(ID.toString());

        // Mocka a busca de status retornando um status com ID 3 (concluído)
        status = new Status(3L, "completed");
        when(statusRepository.findByName(taskItemDto.status())).thenReturn(status);

        // Mocka a busca de usuário pelo UUID retornado
        when(userRepository.findById(ID)).thenReturn(Optional.of(userModel));

        // Cria o UUID da tarefa para atualização
        UUID taskId = UUID.randomUUID();

        // Executa o método a ser testado
        taskService.updateTask(taskId, taskItemDto, token);

        // Verifica se o taskRepository.save() foi chamado e se a conclusão foi definida
        verify(taskRepository, times(1)).save(argThat(taskModel ->
                taskModel.getTaskId().equals(taskId) &&
                        taskModel.getConclusion() != null
        ));
    }

    @Test
    void whenUpdateTaskAndUserNotFoundThenThrowException() {
        // Mocka o token retornando o ID (UUID) como String
        when(token.getName()).thenReturn(ID.toString());

        // Mocka o repositório de usuários para retornar Optional vazio
        when(userRepository.findById(ID)).thenReturn(Optional.empty());

        // Executa o método a ser testado e espera a exceção
        assertThrows(NoSuchElementException.class, () ->
                taskService.updateTask(UUID.randomUUID(), taskItemDto, token)
        );

        // Verifica que o taskRepository.save() não foi chamado
        verify(taskRepository, never()).save(any());
    }
}