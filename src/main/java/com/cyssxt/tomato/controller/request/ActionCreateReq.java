package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.BaseReq;
import lombok.Data;

@Data
public class ActionCreateReq extends BaseReq {
    private String rowId;
    private String contentId;
}
