package com.cyssxt.tomato.dto;

import lombok.Data;

@Data
public class SmsResult {
    Integer code;
    String msg;
    String obj;

    public boolean isSuccess(){
        return code!=null && code==200?true:false;
    }
}
