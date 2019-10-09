package com.cyssxt.tomato.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class TagResultDto {
    String title;
    Boolean actionFlag;
    Boolean tagFlag;
    Byte status;
    Timestamp endTime;
    String rowId;
    Byte contentType;
    List<TagDto> tags;
}
