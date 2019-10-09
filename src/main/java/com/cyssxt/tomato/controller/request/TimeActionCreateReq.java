package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.BaseReq;
import lombok.Data;

@Data
public class TimeActionCreateReq extends ActionCreateReq {

    private String toDoId;
}
