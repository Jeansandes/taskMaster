package com.sandesdev.taskMaster.dtos;

import com.sandesdev.taskMaster.models.Status;

public record TaskItemDto(String title, String description, String status) {
}
