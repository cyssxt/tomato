package com.cyssxt.tomato.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class FileTodoDto {
    String title;
    Integer dateNo;
    Timestamp executeTime;
    Timestamp finishTime;
    String rowId;
}
