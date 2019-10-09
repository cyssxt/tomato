package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.BaseReq;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UpdateActionStatusReq extends BaseReq {

    @NotNull
    @NotEmpty
    private List<String> contentIds;

    private Boolean status=false;
}
