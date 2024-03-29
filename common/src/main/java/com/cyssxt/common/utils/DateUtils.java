package com.cyssxt.common.utils;


import com.cyssxt.common.constant.ErrorMessage;
import com.cyssxt.common.exception.ValidException;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zqy on 18/05/2018.
 */
public class DateUtils {

    public final static String YYYYMMDD = "yyyyMMdd";
    public final static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public final static String YYYY_MM_DD = "yyyy-MM-dd";
    public final static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public final static String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public final static String HH_MM = "HH:mm";
    public static final String HHMM = "HHmm";
    public static final String YYYYMM = "yyyyMM";
    public static final String MM = "MM";

    public static Timestamp getCurrentTimestamp(){
        return new Timestamp(new Date().getTime());
    }

    public static Timestamp getCurrentTimestampBeforeHour(int hour){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,hour);
        return new Timestamp(calendar.getTime().getTime());
    }

    public static String getDataFormatString(Date date,String format,Locale locale){
        SimpleDateFormat sdf = new SimpleDateFormat(format,locale);
        return sdf.format(date);
    }
    public static String getDataFormatString(Date date,String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.SIMPLIFIED_CHINESE);
        return sdf.format(date);
    }

    public static Integer getDataFormatInteger(Date date,String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String result = sdf.format(date);
        Integer time = 0;
        try{
            time = Integer.valueOf(result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return time;
    }
    public static Integer getDataFormatInteger(Calendar date,String format){
        return getDataFormatInteger(date.getTime(),format);
    }
    public static Integer getDataFormatInteger(Calendar date){
        return getDataFormatInteger(date.getTime(),DateUtils.YYYYMMDD);
    }
    public static Integer getDataFormatInteger(Date date){
        return getDataFormatInteger(date,DateUtils.YYYYMMDD);
    }

    public static Integer getCurrentDataFormatInteger(){
        return getDataFormatInteger(new Date(),DateUtils.YYYYMMDD);
    }
    public static String format(Date timestamp,String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(timestamp);
    }
    public static Date strToDate(String date,String format) throws ValidException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            throw new ValidException(ErrorMessage.DATE_PARSE_ERROR.getMessageInfo());
        }
    }

    public static Timestamp getStartTimeOfDay(Date date){
        Calendar start = Calendar.getInstance();
        start.setTime(date);
        start.set(Calendar.HOUR_OF_DAY,0);
        start.set(Calendar.MILLISECOND,0);
        start.set(Calendar.MINUTE,0);
        start.set(Calendar.SECOND,0);
        start.set(Calendar.MILLISECOND,0);
        return new Timestamp(start.getTimeInMillis());
    }
    public static Timestamp getEndTimeOfDay(Date date){
        Calendar start = Calendar.getInstance();
        start.setTime(date);
        start.set(Calendar.HOUR_OF_DAY,23);
        start.set(Calendar.MILLISECOND,59);
        start.set(Calendar.SECOND,59);
        start.set(Calendar.MINUTE,59);
        start.set(Calendar.MILLISECOND,0);
        return new Timestamp(start.getTimeInMillis());
    }
    public static Timestamp dateToTimestamp(Date date){
        return new Timestamp(date.getTime());
    }

    public static String getCurrentDateFormatStr(String format){
        return getDataFormatString(new Date(), format);
    }

    public static void main(String[] args) {
        System.out.println(getEndTimeOfDay(new Date()));
    }
}
