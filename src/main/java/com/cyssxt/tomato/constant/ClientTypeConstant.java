package com.cyssxt.tomato.constant;

public enum ClientTypeConstant {
    IOS((byte)0,""),
    IPAD((byte)1,"");
    private Byte value;
    private String msg;

    ClientTypeConstant(Byte value, String msg) {
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
