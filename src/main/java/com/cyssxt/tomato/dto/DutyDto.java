package com.cyssxt.tomato.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class DutyDto {
    private String rowId;
    private Timestamp createTime;
    private String title;
    private String color;
    public DutyDto(){

    }
    public DutyDto(String rowId, Date createTime, String title, String color) {
        this.rowId = rowId;
        this.createTime = createTime!=null?new Timestamp(createTime.getTime()):null;
        this.title = title;
        this.color = color;
    }
}
