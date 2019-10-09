package com.cyssxt.tomato.dto;

import lombok.Data;

@Data
public class DutyDistributeDto {
    private String tagName;
    private Long time;
    private String title;
    private String rowId;
    private String color;

    public DutyDistributeDto() {
    }

    public DutyDistributeDto(String tagName, Long time) {
        this.tagName = tagName;
        this.time = time;
    }
}
