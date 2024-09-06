package com.sandesdev.taskMaster.models;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tb_task")
public class TaskModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "task_id")
    private UUID taskId;
    @Column(unique = true)
    private String title;
    private  String description;
    private Instant criation;
    private Instant conclusion;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;

    public TaskModel(){}

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCriation() {
        return criation;
    }

    public void setCriation(Instant criation) {
        this.criation = criation;
    }

    public Instant getConclusion() {
        return conclusion;
    }

    public void setConclusion(Instant conclusion) {
        this.conclusion = conclusion;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public void setStatus(Status.Values values) {

    }
}
