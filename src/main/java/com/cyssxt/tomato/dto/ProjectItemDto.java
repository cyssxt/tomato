package com.cyssxt.tomato.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class ProjectItemDto{
    private String title;
    private String todoId;
    private String itemId;
    private Byte status;
    private Timestamp executeTime;
    private Timestamp endTime;
    private Boolean tagFlag;
    private Boolean actionFlag;
    private List<TagDto> tags;
    private Boolean repeatFlag=false;
}
