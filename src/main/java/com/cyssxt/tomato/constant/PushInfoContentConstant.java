package com.cyssxt.tomato.constant;

public enum PushInfoContentConstant {
    TIME_END((byte)0,"倒计时结束","太棒了，您专注了{value}分钟！","",false,"计时结束"),
    EVERY_DAY((byte)1,"","新的一天开始啦，做点什么吧！","",false,"每天早上九点推送"),
    WILL_END((byte)2,"待办截止提醒","有{value}条待办将在今天截至。","",false,"每天早上10：00"),
    TODO_WILL_START((byte)3,"待办开始提醒","还有{value}，您有一条待办即将开始！","",false,"待办开启提醒（个人中设置过提醒方式），按照设置的时间提醒"),
    OVER_THREE_DAY((byte)4,"","您已经3天没有来过了，计划已经荒草丛生。","",false,"超过3天没有打开APP"),
    ;
    private Byte value;
    String title;
    private String text;
    String data;
    private Boolean allFlag;
    private String msg;


    PushInfoContentConstant(Byte value,String title,String text,String data, Boolean allFlag, String msg) {
        this.value = value;
        this.text = text;
        this.allFlag = allFlag;
        this.msg = msg;
        this.title = title;
        this.data = data;
    }

    public static PushInfoContentConstant get(Byte type) {
        if(type==null){
            return null;
        }
        PushInfoContentConstant[] pushInfoContentConstants = PushInfoContentConstant.values();
        for(PushInfoContentConstant pushInfoContentConstant:pushInfoContentConstants){
            if(pushInfoContentConstant.getValue().byteValue()==type.byteValue()){
                return pushInfoContentConstant;
            }
        }
        return null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Byte getValue() {
        return value;
    }

    public void setValue(Byte value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getAllFlag() {
        return allFlag;
    }

    public void setAllFlag(Boolean allFlag) {
        this.allFlag = allFlag;
    }

    public boolean compare(Byte type) {
        if(type!=null && this.getValue().byteValue()==type.byteValue()){
            return true;
        }
        return false;
    }
}
