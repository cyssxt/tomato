package com.cyssxt.tomato.controller.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TimeDistributeReq extends TimeFormatReq {
    @NotNull
    Integer sequence;
    @NotNull
    Byte type;
}
