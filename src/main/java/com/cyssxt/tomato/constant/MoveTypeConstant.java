package com.cyssxt.tomato.constant;

public enum MoveTypeConstant {
    PROJECT((byte)0,"项目"),
    TODO((byte)1,"待办");

    private Byte value;
    private String msg;

    MoveTypeConstant(Byte value, String msg) {
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
