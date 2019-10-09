package com.cyssxt.tomato.dto;

import com.cyssxt.common.bean.Copy;
import com.cyssxt.tomato.controller.request.TodoItemReq;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;


@Data
public class TodoSimpleDto extends Copy {
    private String rowId;
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
    private List<ActionDto> items;
    private Integer repeatUnitValue;
    private Timestamp endTime;
}
