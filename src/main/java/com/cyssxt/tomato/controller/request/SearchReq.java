package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.PageReq;
import lombok.Data;

@Data
public class SearchReq extends PageReq {

    String searchKey;
}
