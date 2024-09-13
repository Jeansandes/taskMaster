package com.sandesdev.taskMaster.controllers;

import com.sandesdev.taskMaster.dtos.LoginRequest;
import com.sandesdev.taskMaster.dtos.LoginResponse;
import com.sandesdev.taskMaster.services.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class TokenControlleTest {
    private static final String EMAIL = "jean@gmail.com";
    private static final String PASSWORD = "123";
    private static final String TOKEN = "jean";
    private static final long INXPIRESIN = 300L;

    @InjectMocks
    private TokenControlle tokenControlle;
    @Mock
    private TokenService tokenService;
    LoginRequest loginRequest;
    LoginResponse loginResponse;
    @BeforeEach
    void setUp() {
        openMocks(this);
        startContent();
    }

    private void startContent() {
        loginRequest = new LoginRequest(EMAIL,PASSWORD);
        loginResponse = new LoginResponse(TOKEN,INXPIRESIN);
    }

    @Test
    void whenLoginThenReturnOk() {
        when(tokenService.checkUser(loginRequest)).thenReturn(loginResponse);

        var response = tokenControlle.login(loginRequest);

        assertEquals(ResponseEntity.ok().build().getStatusCode(), response.getStatusCode());
        assertEquals(loginResponse.accessToken(),response.getBody().accessToken());
        assertEquals(loginResponse.inxpiresIn(),response.getBody().inxpiresIn());
    }
}