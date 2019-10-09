package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.BaseReq;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
public class TimeStopReq extends BaseReq {
    private Timestamp endTime;
    private Long totalTime;
    private String timeId;
}
