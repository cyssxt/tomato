package com.cyssxt.tomato.dto;

import lombok.Data;

@Data
public class PhoneSms {

    private String phoneNumber;
    private String msgCode;
    public PhoneSms(String phoneNumber, String msgCode) {
        this.phoneNumber = phoneNumber;
        this.msgCode = msgCode;
    }
}
