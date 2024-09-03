package com.sandesdev.taskMaster.services;

import com.sandesdev.taskMaster.dtos.TaskItemDto;
import com.sandesdev.taskMaster.repositories.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    private TaskRepository taskRepository;
    private UserServices userServices;

    public void save(TaskItemDto taskDto) {

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
}
