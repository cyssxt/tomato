package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.PageReq;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class TagTodoPageReq extends PageReq {

    @NotEmpty
    @NotNull
    String tagId;

    Byte type;
}
