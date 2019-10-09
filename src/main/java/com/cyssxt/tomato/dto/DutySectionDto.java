package com.cyssxt.tomato.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class DutySectionDto {
    String contentId;
    Byte contentType;
    String title;
    Boolean tagFlag;
    Boolean actionFlag;
    Timestamp executeTime;
    Timestamp endTime;
    Byte status;
    Boolean repeatFlag = false;
    private List<TagDto> tags;
}
