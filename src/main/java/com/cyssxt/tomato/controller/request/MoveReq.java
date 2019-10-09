package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.BaseReq;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class MoveReq extends BaseReq {

    @NotEmpty
    @NotNull
    private String parentId;
    @NotNull
    private Byte parentType;

    @NotNull
    @NotEmpty
    private String contentId;
    public MoveReq(){
    }

    public MoveReq(String contentId,Byte parentType, String parentId) {
        this.parentId = parentId;
        this.parentType = parentType;
        this.contentId = contentId;
    }
}
