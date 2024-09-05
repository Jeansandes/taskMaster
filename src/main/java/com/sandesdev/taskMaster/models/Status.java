package com.sandesdev.taskMaster.models;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "tb_status")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Long statusId;
    private  String name;
    public Status(){}
    public Status(Long statusId, String name){
        this.statusId = statusId;
        this.name = name;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public enum Values{
        PENDING(1L),
        PROGRESS(2L),
        COMPLETED(3L);

        Long statusId;
        Values(Long statusId){
            this.statusId = statusId;
        }

    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Status status = (Status) o;
        return Objects.equals(name, status.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
