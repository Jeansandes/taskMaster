package com.sandesdev.taskMaster;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "task master", version = "1", description = "gerenciamento de tarefas"))
public class TaskMasterApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskMasterApplication.class, args);
	}

}
