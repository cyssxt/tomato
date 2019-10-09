package com.cyssxt.tomato.controller.request;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UserCreateReq extends ActionCreateReq {
    String imgUrl;
    @NotNull
    @NotEmpty
    String title;
    @NotNull
    @NotEmpty
    String introduce;
}
