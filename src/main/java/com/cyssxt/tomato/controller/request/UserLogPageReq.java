package com.cyssxt.tomato.controller.request;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserLogPageReq extends ActionPageReq {
    @NotNull
    private Integer monthNo;

    private Byte orderType;
}

