package com.cyssxt.tomato.constant;

public enum UserLogOrderTypeConstant {
    CREATE_TIME((byte)0,"创建时间"),
    UPDATE_TIME((byte)1,"更新时间");
    private Byte value;
    private String msg;

    UserLogOrderTypeConstant(Byte value, String msg) {
        this.value = value;
        this.msg = msg;
    }

    public boolean compare(Byte type){
        return type!=null&&this.getValue().byteValue()==type.byteValue();
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
