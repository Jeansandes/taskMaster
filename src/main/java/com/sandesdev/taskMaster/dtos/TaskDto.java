package com.sandesdev.taskMaster.dtos;

import java.util.List;

public record TaskDto(List<TaskResponse> taskItens,
                      int page,
                      int pageSize,
                      int totalPages,
                      long totalElements) {
}
