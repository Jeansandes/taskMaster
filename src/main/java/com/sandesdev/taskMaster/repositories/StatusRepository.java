package com.sandesdev.taskMaster.repositories;

import com.sandesdev.taskMaster.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {
    Status findByName(String name);
}
