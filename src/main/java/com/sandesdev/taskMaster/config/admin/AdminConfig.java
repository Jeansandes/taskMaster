package com.sandesdev.taskMaster.config.admin;

import com.sandesdev.taskMaster.models.Role;
import com.sandesdev.taskMaster.models.UserModel;
import com.sandesdev.taskMaster.repositories.RoleRepository;
import com.sandesdev.taskMaster.repositories.UserRepository;
import jakarta.transaction.Transactional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;

import java.util.Set;
import java.util.logging.Logger;


@Configuration
public class AdminConfig implements CommandLineRunner {
    private static final Logger logger =Logger.getLogger(AdminConfig.class.getName());
    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public AdminConfig(RoleRepository roleRepository, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var adminRole = roleRepository.findByName(Role.Values.ADMIN.name());
        var userModel = userRepository.findByName("jean sandes");
        userModel.ifPresentOrElse(
                user -> {
                    //userRepository.delete(user);
                    logger.info("Admin já existe!");
                },
                () -> {
                    var user = new UserModel();
                    user.setName("jean sandes");
                    user.setEmail("sandesjean.sandes@gmail.com");
                    user.setPassword(passwordEncoder.encode("123"));
                    user.setData(Instant.now());
                    user.setRoles(Set.of(adminRole));
                    userRepository.save(user);
                }
        );
    }
}
