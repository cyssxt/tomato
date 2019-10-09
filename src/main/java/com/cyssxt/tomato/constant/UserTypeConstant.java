package com.cyssxt.tomato.constant;

public enum UserTypeConstant {
    GUEST((byte)0,"游客"),
    NORMAL((byte)1,"正式"),
    ;

    private Byte value;
    private String msg;

    UserTypeConstant(Byte value, String msg) {
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
