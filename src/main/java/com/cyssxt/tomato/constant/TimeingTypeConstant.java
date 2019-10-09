package com.cyssxt.tomato.constant;

public enum TimeingTypeConstant {
    POS((byte)0,"正序"),
    NEG((byte)1,"倒叙"),
    ;

    private Byte value;
    private String msg;

    TimeingTypeConstant(Byte value, String msg) {
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
