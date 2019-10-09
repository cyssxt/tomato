package com.cyssxt.tomato.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectSectionDto {
    private String title;
    private Byte status;
    private String todoId;
    private Boolean actionFlag;
    private Boolean tagFlag;
    private Date executeTime;
    private Integer sort;

    public ProjectSectionDto(String title, Byte status, String todoId, Boolean actionFlag, Boolean tagFlag, Date executeTime,Integer sort) {
        this.title = title;
        this.status = status;
        this.todoId = todoId;
        this.actionFlag = actionFlag;
        this.tagFlag = tagFlag;
        this.executeTime = executeTime;
        this.sort = sort;
    }
}
