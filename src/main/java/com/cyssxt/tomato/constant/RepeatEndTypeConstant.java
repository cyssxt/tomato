package com.cyssxt.tomato.constant;

public enum RepeatEndTypeConstant {
    NONE((byte)0,"到期结束"),
    END((byte)1,"不结束"),
    ;
    Byte value;
    String msg;

    RepeatEndTypeConstant(Byte value, String msg) {
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
        if(value!=null && value.byteValue()==this.getValue().byteValue()){
            return true;
        }
        return false;
    }
}
