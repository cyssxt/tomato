package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.BaseReq;
import lombok.Data;

@Data
public class TimeFormatReq extends BaseReq {
    String format="MM.dd";
}
