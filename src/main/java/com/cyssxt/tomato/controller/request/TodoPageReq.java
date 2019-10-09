package com.cyssxt.tomato.controller.request;

import lombok.Data;

import java.util.List;

@Data
public class TodoPageReq extends ActionPageReq {

    private String dateNo;

    //是否是收件箱
    private Byte pageType;

    private String projectId;

    //截止时间戳
    private Integer endDateNo;
    private List<String> parentIds;
    private Byte parentType;
    private Byte notStatus=2;
    private Integer endExecuteDateNo;

}
