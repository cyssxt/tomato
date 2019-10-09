package com.cyssxt.tomato.dto;

import lombok.Data;

import java.util.List;

@Data
public class ParentDto {
    String title;
    Byte contentType;
    String rowId;
    List<ProjectDto> childs;
}
