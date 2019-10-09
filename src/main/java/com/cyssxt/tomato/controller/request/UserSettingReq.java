package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.BaseReq;
import lombok.Data;

@Data
public class UserSettingReq extends BaseReq {

    private Byte remindType;
    private Byte timeType;
    private Integer timeValue;
    private Boolean orderCal;
}
