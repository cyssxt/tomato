package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.BaseReq;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class TimeCreateTodoReq extends BaseReq {
    @NotNull
    @NotEmpty
    private String timeId;
    @NotNull
    private Integer degree;
    private String title;
    private String dutyId;
    private String todoId;
}
