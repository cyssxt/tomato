package com.cyssxt.tomato.constant;

public enum ContentTypeConstant {
    TODO((byte)0,"待办"),
    PROJECT((byte)1,"项目"),
    DUTY((byte)2,"责任"),
    LOG((byte)3,"日志"),
    INBOX((byte)-1,"收件箱"),
    NOPARENT((byte)-2,"没有父待办"),
    TAG((byte)4, "标签"), USRLOG((byte)5, "用户日志");
    private Byte value;
    private String msg;

    ContentTypeConstant(Byte value, String msg) {
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

    public boolean compare(Byte type) {
        if(type!=null && type.byteValue()==this.getValue().byteValue()){
            return true;
        }
        return false;
    }
}
