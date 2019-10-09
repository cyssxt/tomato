package com.cyssxt.tomato.controller.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FinishListReq extends TimeFormatReq {
    Integer sequence;
    @NotNull
    Byte type;
}
