package com.sandesdev.taskMaster.dtos;

import java.time.Instant;
import java.util.UUID;

public record TaskResponse(UUID id, String title, String description, String status, Instant criation, Instant conclusion) {
}
