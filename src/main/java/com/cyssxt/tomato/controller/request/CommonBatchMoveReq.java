package com.cyssxt.tomato.controller.request;

import com.cyssxt.tomato.dto.BatchReq;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CommonBatchMoveReq extends BatchReq {
    private String parentId;
    private Byte parentType;
}
