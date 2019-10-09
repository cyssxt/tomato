package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.BaseReq;
import lombok.Data;

@Data
public class TimeEndReq extends BaseReq {

    private Integer concentrationDegree;
    private String dutyId;
    private String todoTitle;
    private String timeId;
    private Integer totalTime;
}
