package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.BaseReq;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class TimeStartReq extends BaseReq {
    Timestamp startTime;
    String todoId;
    String timeId;
    String childId;
}
