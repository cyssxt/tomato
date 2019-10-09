package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.BaseReq;
import com.cyssxt.common.request.PageReq;
import lombok.Data;

@Data
public class IndexReq extends PageReq {

    String searchKey;
}
