package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.BaseReq;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CancelReq extends BaseReq {
    @NotNull
    @NotEmpty
    String contentId;
}
