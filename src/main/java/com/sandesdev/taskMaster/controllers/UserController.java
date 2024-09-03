package com.sandesdev.taskMaster.controllers;

import com.sandesdev.taskMaster.dtos.UserDto;
import com.sandesdev.taskMaster.services.UserServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@ResponseBody
@RequestMapping("/users")
public class UserController {
    private UserServices userServices;

    public UserController(UserServices userServices) {
        this.userServices = userServices;
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody UserDto userDto){
        userServices.save(userDto);
        return ResponseEntity.ok().build();
    }
}
