package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.BaseReq;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ForecastReq extends BaseReq {

    private String dateStr;
}
