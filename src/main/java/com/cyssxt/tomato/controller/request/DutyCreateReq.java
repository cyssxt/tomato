package com.cyssxt.tomato.controller.request;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class DutyCreateReq extends ActionCreateReq {

    @NotNull
    @NotEmpty
    private String title;
    @NotNull
    @NotEmpty
    private String color;
}
