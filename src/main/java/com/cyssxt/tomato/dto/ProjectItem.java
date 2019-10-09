package com.cyssxt.tomato.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProjectItem {
    private String title;
    private String todoId;
    private String itemId;
    public ProjectItem(){}
    public ProjectItem(String todoId) {
        this.todoId = todoId;
    }
}
