package com.cyssxt.tomato.controller.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class TimePauseReq extends TimeStopReq{
    @NotNull
    @NotEmpty
    private String timeId;
}
