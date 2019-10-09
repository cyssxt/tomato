package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.BaseReq;
import lombok.Data;

@Data
public class MonthLogReq extends BaseReq {
    Integer monthNo;
    Byte orderType;
}
