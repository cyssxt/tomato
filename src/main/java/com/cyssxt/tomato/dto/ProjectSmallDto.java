package com.cyssxt.tomato.dto;

import com.cyssxt.tomato.entity.ProjectSectionEntity;
import com.cyssxt.tomato.entity.ToDosEntity;
import lombok.Data;

import java.util.List;

@Data
public class ProjectSmallDto {
    private ProjectSectionEntity smallTitleEntity;
    private List<ToDosEntity> toDosEntityList;
}
