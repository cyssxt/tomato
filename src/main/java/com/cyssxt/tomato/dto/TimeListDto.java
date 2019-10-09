package com.cyssxt.tomato.dto;

import com.cyssxt.tomato.dao.TimeService;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;

@Data
public class TimeListDto {
    String title;
    Timestamp startTime;
    Timestamp endTime;
    Long totalTime;
    Integer degree;
    String color;

    public String getColor() {
        if(StringUtils.isEmpty(color)){
           this.color= TimeService.DEFAULT_COLOR;
        }
        return this.color;
    }
}
