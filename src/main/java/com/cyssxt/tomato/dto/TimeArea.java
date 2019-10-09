package com.cyssxt.tomato.dto;

import com.cyssxt.common.filters.DefaultFilter;
import com.cyssxt.common.utils.DateUtils;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Data
public class TimeArea extends DefaultFilter {
    Integer startDateNo;
    Integer endDateNo;
    Date startTime;
    Date endTime;
    String startDateNoStr;
    String endDateNoStr;
    String format;

    @Override
    public String[] getExcludeFields() {
        return new String[]{"format"};
    }

    public String getStartDateNoStr() {
        if(StringUtils.isEmpty(format)){
            return "";
        }
        return DateUtils.getDataFormatString(startTime,format);
    }

    public String getEndDateNoStr() {
        if(StringUtils.isEmpty(format)){
            return "";
        }
        return DateUtils.getDataFormatString(endTime,format);
    }

    public TimeArea(Date startTime, Date endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Integer getStartDateNo() {
        return DateUtils.getDataFormatInteger(startTime);
    }

    public Integer getEndDateNo() {
        return DateUtils.getDataFormatInteger(endTime);
    }

    public Timestamp getStart(){
        return DateUtils.getStartTimeOfDay(this.startTime);
    }
    public Timestamp getEnd(){
        return DateUtils.getEndTimeOfDay(this.endTime);
    }
    public TimeArea() {

    }

    public static void main(String[] args) {
        System.out.println(DateUtils.getEndTimeOfDay(new Date()));
    }
}
