package com.cyssxt.tomato.dto;

import com.cyssxt.common.request.BaseReq;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class BatchReq extends BaseReq {
    @NotNull
    @NotEmpty
    private List<BatchContentItem> items;

}
