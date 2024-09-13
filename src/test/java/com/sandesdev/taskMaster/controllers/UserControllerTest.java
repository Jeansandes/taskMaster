package com.sandesdev.taskMaster.controllers;

import com.sandesdev.taskMaster.dtos.UserDto;
import com.sandesdev.taskMaster.services.UserServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class UserControllerTest {
    private static final String NAME = "jean";
    private static final String EMAIL = "jean@gmail.com";
    private static final String PASSWORD = "123";
    @InjectMocks
    private UserController userController;
    @Mock
    private UserServices userServices;

    UserDto userDto;
    @BeforeEach
    void setUp() {
        openMocks(this);
        startContent();
    }

    private void startContent() {
        userDto =new UserDto(NAME,EMAIL,PASSWORD);
    }

    @Test
    void whenCreateUserThenReturnOK() {
       doNothing().when(userServices).save(any());

       var response = userController.createUser(userDto);

        assertEquals(ResponseEntity.ok().build(), response);
        verify(userServices,times(1)).save(userDto);
        verifyNoMoreInteractions(userServices);
    }
}