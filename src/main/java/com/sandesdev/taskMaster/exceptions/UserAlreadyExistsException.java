package com.sandesdev.taskMaster.exceptions;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String s){
        super(s);
    }
}
