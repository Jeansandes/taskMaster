package com.sandesdev.taskMaster.services;

import com.sandesdev.taskMaster.config.admin.AdminConfig;
import com.sandesdev.taskMaster.dtos.UserDto;
import com.sandesdev.taskMaster.exceptions.UserAlreadyExistsException;
import com.sandesdev.taskMaster.models.Role;
import com.sandesdev.taskMaster.models.UserModel;
import com.sandesdev.taskMaster.repositories.RoleRepository;
import com.sandesdev.taskMaster.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.logging.Logger;

@Service
public class UserServices {
    private static final Logger logger =Logger.getLogger(AdminConfig.class.getName());
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private KafkaTemplate<String, UserDto> kafkaTemplate;

    public UserServices(UserRepository userRepository, RoleRepository roleRepository
            ,BCryptPasswordEncoder passwordEncoder,KafkaTemplate<String, UserDto> kafkaTemplate) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public void save(UserDto userDto) {
        int partition = 1;
        var basicRole = roleRepository.findByName(Role.Values.BASIC.name());
        var user = userRepository.findByName(userDto.name());
        if(user.isPresent()){
            throw new UserAlreadyExistsException("Usuário já existe");
        }
        UserModel userModel = new UserModel();
        userModel.setName(userDto.name());
        userModel.setEmail(userDto.email());
        userModel.setPassword(passwordEncoder.encode(userDto.password()));
        userModel.setData(Instant.now());
        userModel.setRoles(Set.of(basicRole));
        userRepository.save(userModel);
        sendMessage(new UserDto(userDto.name(),userDto.email(),userModel.getPassword()),partition);
    }
    public void sendMessage(UserDto userDto, int parition){
        logger.info("mensagem enviada para partição: "+parition);
        kafkaTemplate.send("taskMaster_email_kafka",parition,null, userDto);
    }
}
