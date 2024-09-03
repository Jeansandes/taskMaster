package com.sandesdev.taskMaster.dtos;

import java.util.List;

public record TaskDto(List<TaskItemDto> taskItens,
                      int page,
                      int pageSize,
                      int totalPages,
                      long totalElements) {
}
