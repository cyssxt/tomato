package com.cyssxt.tomato.constant;

public enum ReminderTypeConstant {
    ON_TIME((byte)0,"准时"),
    B_5_M((byte)1,"提前5分钟"),
    B_30_M((byte)2,"提前30分钟"),
    B_1_H((byte)3,"提前1小时"),
    B_1_D((byte)4,"提前1天"),
    B_2_D((byte)5,"提前两天"),
    ;

    private Byte value;
    private String msg;

    ReminderTypeConstant(Byte value, String msg) {
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
}
