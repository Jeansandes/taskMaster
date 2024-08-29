package com.sandesdev.taskMaster.models;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tb_users")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID userId;
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    private Instant data;
    @ManyToMany
    @JoinTable(
            name = "tb_users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "roleId")
    )
    private Set<Role> roles;

    public UserModel( String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
    public UserModel(){}

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Instant getData() {
        return data;
    }

    public void setData(Instant data) {
        this.data = data;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
