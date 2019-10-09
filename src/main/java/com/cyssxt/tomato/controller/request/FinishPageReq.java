package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.PageReq;
import lombok.Data;

import java.util.List;

@Data
public class FinishPageReq extends PageReq {
    List<String> tagIds;
}
