package com.cyssxt.tomato.controller.request;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class TodoCreateReq extends ActionCreateReq {
    private String title;
    private Timestamp executeTime;
    private Boolean remindFlag;
    private String parentId;
    private Byte parentType;
    private String smallTitle;
    private Byte status;
    private Integer planTime;
    private Integer concentrationDegree;
    private String smallId;
    private Integer sort;
    private String content;
    private Timestamp nextExecuteTime;
    private Boolean repeatFlag;
    private Byte repeatType;
    private Byte repeatUnit;
    private Byte repeatEndType;
    private Timestamp repeatEndTime;
    private Byte[] repeatExecDays;
    private List<String> tagIds;
    private List<TodoItemReq> items;
    private Integer repeatUnitValue;
    private Boolean timeFlag;
    private Timestamp endTime;
    private Boolean updateFlag;
    private Boolean allFlag;
}
