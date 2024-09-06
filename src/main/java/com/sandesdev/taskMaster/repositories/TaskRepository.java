package com.sandesdev.taskMaster.repositories;

import com.sandesdev.taskMaster.models.TaskModel;

import com.sandesdev.taskMaster.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, UUID> {

    Optional<TaskModel> findByTitle(String title);

    Page<TaskModel> findAllByUser(UserModel user, PageRequest criation);
}
