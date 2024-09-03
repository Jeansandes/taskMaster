package com.sandesdev.taskMaster.controllers;

import com.sandesdev.taskMaster.dtos.LoginRequest;
import com.sandesdev.taskMaster.dtos.LoginResponse;
import com.sandesdev.taskMaster.services.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenControlle {
    private TokenService tokenService;

    public TokenControlle(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        var loginResponse = tokenService.checkUser(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }
}
