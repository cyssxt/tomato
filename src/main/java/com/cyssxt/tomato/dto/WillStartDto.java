package com.cyssxt.tomato.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class WillStartDto {
    private Long remindMinute;
    String clientId;
    String todoId;
    Timestamp executeTime;
    String rowId;
    public String getValue(){
        if(remindMinute==null){
            return "0";
        }
        if(remindMinute==60){
            return "1小时";
        }
        if(remindMinute==24*60){
            return "1天";
        }
        if(remindMinute==24*60*2){
            return "2天";
        }
        return "";
    }
}
