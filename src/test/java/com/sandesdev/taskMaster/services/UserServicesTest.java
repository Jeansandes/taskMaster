package com.sandesdev.taskMaster.services;

import com.sandesdev.taskMaster.dtos.UserDto;
import com.sandesdev.taskMaster.exceptions.UserAlreadyExistsException;
import com.sandesdev.taskMaster.models.Role;
import com.sandesdev.taskMaster.models.UserModel;
import com.sandesdev.taskMaster.repositories.RoleRepository;
import com.sandesdev.taskMaster.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.PASSWORD;

class UserServicesTest {
    private static final UUID ID = UUID.randomUUID();
    private static final String EMAIL = "jean";
    private static final String NAME = "jean@gmail.com";
    private static final String PASSWORDENCODER = "123";
    @InjectMocks
    private UserServices userServices;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private KafkaTemplate<String, UserDto> kafkaTemplate;
    UserDto userDto;
    UserModel userModel;
    Role basicRole;
    @BeforeEach
    void setUp() {
        openMocks(this);
        startContent();
    }

    private void startContent() {
        userDto = new UserDto(NAME, EMAIL,PASSWORD);
        userModel = new UserModel(ID,NAME,EMAIL,PASSWORD);
        basicRole = new Role(1L, "basic");
    }

    @Test
    void whenSaveThenReturnSave() {
        when(roleRepository.findByName(any())).thenReturn(basicRole);
        when(userRepository.findByName(NAME)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn(PASSWORDENCODER);

        userServices.save(userDto);

        verify(userRepository, times(1)).findByName(userDto.name());
        verify(userRepository,times(1)).save(argThat(userModel -> userModel.getPassword().equals(PASSWORDENCODER)));
        verify(userRepository,times(1)).save(argThat(userModel -> userModel.getRoles().contains(basicRole)));
        verify(userRepository,times(1)).save(argThat(userModel -> userModel.getEmail().equals(EMAIL)));


    }
    @Test
    void whenSaveThenReturnUserAlreadyExistsException(){
        when(roleRepository.findByName(any())).thenReturn(basicRole);
        when(userRepository.findByName(NAME)).thenReturn(Optional.of(userModel));
        try {
            userServices.save(userDto);
        }catch (Exception e){
            assertEquals(UserAlreadyExistsException.class,e.getClass());
            assertEquals("usuário já existe!",e.getMessage());
        }


    }

    @Test
    void whenSendMessageOk() {
        Integer partition = 1;
        userServices.sendMessage(userDto, partition);

        verify(kafkaTemplate).send(eq("taskMaster_email_kafka"), eq(partition), eq(null), eq(userDto));
    }
}