package com.cyssxt.tomato.constant;


import java.util.Calendar;

public enum RepeatUnitConstant {
    DAY((byte)0,"天",Calendar.DATE,null),
    WEEK((byte)1,"周", Calendar.WEEK_OF_MONTH,Calendar.DAY_OF_WEEK),
    MONTH((byte)2,"月",Calendar.MONTH,Calendar.DAY_OF_MONTH),
    ;

    private Byte value;
    private String msg;
    private Integer timeUnit;
    private Integer firstDay;

    RepeatUnitConstant(Byte value, String msg,Integer timeUnit,Integer firstDay) {
        this.value = value;
        this.msg = msg;
        this.timeUnit = timeUnit;
        this.firstDay = firstDay;
    }

    public Integer getFirstDay() {
        return firstDay;
    }

    public void setFirstDay(Integer firstDay) {
        this.firstDay = firstDay;
    }

    public Integer getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(Integer timeUnit) {
        this.timeUnit = timeUnit;
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

    public boolean compare(Byte value){
        if(value!=null && this.value.byteValue()==value.byteValue()){
            return true;
        }
        return false;
    }
}
