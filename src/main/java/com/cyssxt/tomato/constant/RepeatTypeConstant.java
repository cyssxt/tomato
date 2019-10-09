package com.cyssxt.tomato.constant;

public enum RepeatTypeConstant {
    NONE((byte)0,"无"),
    EVERY_DAY((byte)1,"每天"),
    EVERY_WEEK((byte)2,"每周（周一）"),
    EVERY_WORK_DAY((byte)3,"每周工作日"),
    EVERY_MONTH((byte)4,"每月（3号）"),
    USER_DEFINED((byte)5,"自定义"),
    ;

    private Byte value;
    private String msg;

    RepeatTypeConstant(Byte value, String msg) {
        this.value = value;
        this.msg = msg;
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
        return value!=null && this.getValue().byteValue()==value.byteValue();
    }

    public RepeatTypeConstant get(Byte type){
        if( type!=null ) {
            RepeatTypeConstant[] repeatTypeConstants = RepeatTypeConstant.values();
            for (RepeatTypeConstant repeatTypeConstant : repeatTypeConstants) {
                if (repeatTypeConstant.getValue().byteValue()==type.byteValue()){
                    return repeatTypeConstant;
                }
            }
        }
        return null;
    }
}
