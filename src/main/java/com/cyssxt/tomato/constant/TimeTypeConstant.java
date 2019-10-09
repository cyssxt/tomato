package com.cyssxt.tomato.constant;

import com.cyssxt.common.exception.ValidException;
import com.cyssxt.tomato.errors.MessageCode;

import java.util.Calendar;

public enum TimeTypeConstant {
    DAY((byte)-1,"天",Calendar.DATE,0),
    WEEK((byte)0,"周", Calendar.WEEK_OF_MONTH,Calendar.DAY_OF_WEEK),
    MONTH((byte)1,"月",Calendar.MONTH,Calendar.DAY_OF_MONTH),
    YEAR((byte)2,"年",Calendar.YEAR,Calendar.DAY_OF_YEAR);
    private Byte value;
    private String msg;
    private int unit;
    private int dayUnit;

    TimeTypeConstant(Byte value, String msg,int unit,int dayUnit) {
        this.value = value;
        this.msg = msg;
        this.unit  = unit;
        this.dayUnit = dayUnit;
    }

    public static TimeTypeConstant get(Byte type) throws ValidException {
        if(type==null){
            throw new ValidException(MessageCode.DATE_TYPE_NOT_BE_NULL);
        }
        TimeTypeConstant[] timeTypeConstants = TimeTypeConstant.values();
        for(TimeTypeConstant timeTypeConstant:timeTypeConstants){
            if(timeTypeConstant.getValue().byteValue()==type.byteValue()){
                return timeTypeConstant;
            }
        }
        throw new ValidException(MessageCode.DATE_TYPE_CANNOT_FOUND);
    }

    public int getDayUnit() {
        return dayUnit;
    }

    public void setDayUnit(int dayUnit) {
        this.dayUnit = dayUnit;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public Byte getValue() {
        return value;
    }

    public void setValue(Byte value) {
        this.value = value;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean compare(Byte type){
        if(type!=null && type.byteValue()==this.getValue().byteValue()){
            return true;
        }
        return false;
    }
}
