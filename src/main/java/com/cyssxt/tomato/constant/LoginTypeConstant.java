package com.cyssxt.tomato.constant;

public enum LoginTypeConstant {
    ACCOUNT((byte)0,"账号"),
    WECHAT((byte)1,"微信"),
    QQ((byte)2,"QQ"),
    SINA((byte)3,"新浪"),
    ;

    private Byte value;
    private String msg;

    LoginTypeConstant(Byte value, String msg) {
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
