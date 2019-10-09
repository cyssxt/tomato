package com.cyssxt.tomato.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TimeCountDto {
    private Long totalTime;
    private Long time;
    Timestamp minStartTime;
    Timestamp maxEndTime;
}
