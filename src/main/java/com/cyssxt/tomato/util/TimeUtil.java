package com.cyssxt.tomato.util;

import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.utils.DateUtils;
import com.cyssxt.tomato.constant.TimeTypeConstant;
import com.cyssxt.tomato.dto.TimeArea;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

public class TimeUtil {
    public static TimeArea getTimArea(Integer monthNo) throws ValidException {
        Date date = DateUtils.strToDate(monthNo+"",DateUtils.YYYYMM);
        Calendar now = Calendar.getInstance();
        Calendar monthDate = Calendar.getInstance();
        monthDate.setTime(date);
        int result = now.get(Calendar.MONTH) - monthDate.get(Calendar.MONTH);
        int month = (now.get(Calendar.YEAR) - monthDate.get(Calendar.YEAR)) * 12;
        int sequence = month+result;
        return getTimeArea(-1*sequence,TimeTypeConstant.MONTH);
    }

    public static TimeArea getTimeArea(Integer sequence, TimeTypeConstant type){
        Calendar now = Calendar.getInstance();
        final Date startTime;
        final Date endTime;
        sequence = Optional.ofNullable(sequence).orElse(0);
        if(sequence!=0) {
            int timeUnit = type.getUnit();
            now.add(timeUnit,sequence);
        }
        int dayUnit = type.getDayUnit();
        if(dayUnit!=0) {
            now.set(dayUnit, 1);
        }
        startTime = now.getTime();
        now.add(type.getUnit(),1);
        if(dayUnit!=0) {
            now.add(dayUnit, -1);
        };
        endTime = now.getTime();
        return new TimeArea(startTime,endTime);
    }

    public static String getAliasName(Timestamp time){
        if(null==time){
            return "";
        }
        String dateStr = DateUtils.getDataFormatString(time,DateUtils.YYYYMMDD);
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE,1);
        String nowStr = DateUtils.getDataFormatString(now.getTime(),DateUtils.YYYYMMDD);
        if(nowStr.equals(dateStr)){
            return "明天";
        }
        return DateUtils.getDataFormatString(time,"EEEE");
    }

    public static void main(String[] args) {
        System.out.println(getTimeArea(0,TimeTypeConstant.WEEK));
    }
}
