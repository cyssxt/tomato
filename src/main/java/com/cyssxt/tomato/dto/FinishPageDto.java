package com.cyssxt.tomato.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class FinishPageDto {

    private Integer dateNo;
    private Timestamp finishTime;
    private String rowId;
    private String title;
    private Integer days;
    private Timestamp startTime;
}
