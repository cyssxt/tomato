package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.BaseReq;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TimeListReq extends BaseReq {
    @NotNull
    private Integer dateNo;
}
