package com.sandesdev.taskMaster.services;

import com.sandesdev.taskMaster.dtos.UserDto;
import com.sandesdev.taskMaster.exceptions.UserAlreadyExistsException;
import com.sandesdev.taskMaster.models.Role;
import com.sandesdev.taskMaster.models.UserModel;
import com.sandesdev.taskMaster.repositories.RoleRepository;
import com.sandesdev.taskMaster.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
public class UserServices {
    private ModelMapper mapper;
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    public UserServices(ModelMapper mapper, UserRepository userRepository, RoleRepository roleRepository) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void save(UserDto userDto) {
        var basicRole = roleRepository.findByName(Role.Values.BASIC.name());
        var user = userRepository.findByName(userDto.name());
        if(user.isPresent()){
            throw new UserAlreadyExistsException("Usuário já existe");
        }
        UserModel userModel = new UserModel();
        mapper.map(userDto,userModel);
        userModel.setData(Instant.now());
        userModel.setRoles(Set.of(basicRole));
        userRepository.save(userModel);
    }
}
