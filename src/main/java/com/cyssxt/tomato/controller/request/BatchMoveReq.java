package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.BaseReq;
import com.cyssxt.tomato.dto.BatchReq;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class BatchMoveReq extends BatchReq {
    @NotEmpty
    @NotNull
    private String parentId;
    @NotNull
    private Byte type;

    @NotNull
    @NotEmpty
    private List<String> contentIds;
}
