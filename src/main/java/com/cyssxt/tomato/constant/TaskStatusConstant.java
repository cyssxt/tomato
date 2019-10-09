package com.cyssxt.tomato.constant;

public enum TaskStatusConstant {
    WAIT((byte)0,"等待"),
    RUNNING((byte)1,"运行中"),
    FINISH((byte)2,"完成"),
    ;

    private Byte value;
    private String msg;

    TaskStatusConstant(Byte value, String msg) {
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

    public boolean compare(Byte status) {
        if(status!=null && status.byteValue()==this.getValue().byteValue()){
            return true;
        }
        return false;
    }
}
