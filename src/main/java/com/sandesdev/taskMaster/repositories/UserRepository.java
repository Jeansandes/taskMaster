package com.sandesdev.taskMaster.repositories;

import com.sandesdev.taskMaster.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserModel, UUID> {
    Optional<UserModel> findByName(String name);

    Optional<UserModel> findByEmail(String email);
}
