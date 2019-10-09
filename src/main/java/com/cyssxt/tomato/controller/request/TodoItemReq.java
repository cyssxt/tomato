package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.BaseReq;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class TodoItemReq extends BaseReq {
    @NotEmpty
    @NotNull
    private String content;
    private Byte status=0;
    private String actionId;
    private Byte itemType=1;//1表示动作0表示笔记
}
