package com.cyssxt.tomato.dto;

import lombok.Data;

@Data
public class CustomMsgDto<T> {

    Byte type=1;

    T data;

    public CustomMsgDto(Byte type) {
        this.type = type;
    }

    public CustomMsgDto(Byte type, T data) {
        this.type = type;
        this.data = data;
    }

    public CustomMsgDto(){

    }
}
