package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.BaseReq;
import lombok.Data;

@Data
public class UserInfoUpdateReq extends BaseReq {
    private String userName;
    private String userIcon;
    private String introduce;
    private String banner;
}
