package com.sandesdev.taskMaster.controllers;

import com.sandesdev.taskMaster.dtos.UserDto;
import com.sandesdev.taskMaster.services.UserServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@ResponseBody
@RequestMapping(value = "/users", produces = {"aplication/json"})
@Tag(name = "task-master")
public class UserController {
    private UserServices userServices;

    public UserController(UserServices userServices) {
        this.userServices = userServices;
    }

    @Operation(summary = "cadastra um novo usuário", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "usuário cadastrado com sucesso")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createUser(@RequestBody UserDto userDto){
        userServices.save(userDto);
        return ResponseEntity.ok().build();
    }
}
