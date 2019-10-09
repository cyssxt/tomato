package com.cyssxt.tomato.dto;

import com.cyssxt.common.utils.DateUtils;
import com.cyssxt.tomato.util.TimeUtil;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

@Data
public class TodoPlanDto extends ToDoDto{
    Integer dayInfo;
    Integer dateNo;
    Timestamp date;
    List<ToDoDto> todos;
    Byte contentType;

    public String getDayNo(){
        Timestamp timestamp = this.getExecuteTime();
        if(timestamp==null){
            return "";
        }
        return DateUtils.getDataFormatString(timestamp,"d号");
    }

    public String getAlias(){
        return TimeUtil.getAliasName(this.getExecuteTime());
    }
}
