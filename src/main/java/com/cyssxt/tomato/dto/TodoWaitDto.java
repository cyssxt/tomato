package com.cyssxt.tomato.dto;

import lombok.Data;

import java.util.List;

@Data
public class TodoWaitDto extends ToDoDto{
    List<ToDoDto> childs;
    private Byte contentType;
    private String parentId;
}
