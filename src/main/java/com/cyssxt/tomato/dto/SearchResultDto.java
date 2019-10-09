package com.cyssxt.tomato.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class SearchResultDto {
    Timestamp createTime;
    String title;
    String rowId;
    Byte type;
    String projectTitle;
    Boolean tagFlag;
    Boolean actionFlag;
    Byte status;
}
