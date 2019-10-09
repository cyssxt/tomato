package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.BaseReq;
import lombok.Data;

import java.util.List;

@Data
public class BatchDelReq extends BaseReq {

    private List<String> contentIds;
}
