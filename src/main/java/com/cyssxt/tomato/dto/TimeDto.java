package com.cyssxt.tomato.dto;

import lombok.Data;

@Data
public class TimeDto {
    Long count;
    Long time;
    String timeId;
    Long projectNum;

    public TimeDto(Long count, Long time) {
        this.count = count;
        this.time = time;
    }

    public TimeDto(Long count, Long time, Long projectNum) {
        this.count = count;
        this.time = time;
        this.projectNum = projectNum;
    }

    public TimeDto(String timeId) {
        this.timeId = timeId;
    }
    public TimeDto(){}
}
