package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.BaseReq;
import lombok.Data;

import java.util.List;

@Data
public class TagFilterReq extends BaseReq {

    List<String> tagIds;
}
