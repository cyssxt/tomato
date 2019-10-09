package com.cyssxt.tomato.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
public class ToDoDto {

    private String rowId;
    private String title;
    private Timestamp executeTime;
    private Timestamp endTime;
    private List<TagDto> tags;
    private List<TodoActionDto> actions;
    private Boolean tagFlag;
    private Boolean actionFlag;
    private String parentId;
    private Byte parentType;
    private Byte status;
    private String childId;
    private Integer dateNo;
    private Boolean repeatFlag;
    private Timestamp repeatEndTime;
    private Byte contentType;
    public String getTodoId(){
        return rowId;
    }

    public ToDoDto(){

    }
    public ToDoDto(String rowId, String title) {
        this.rowId = rowId;
        this.title = title;
    }

    public ToDoDto(String rowId, String title, Timestamp executeTime, Timestamp endTime) {
        this.rowId = rowId;
        this.title = title;
        this.executeTime = executeTime;
        this.endTime = endTime;
    }
    public ToDoDto(String rowId, String title, Date executeTime){
        this.rowId = rowId;
        this.title = title;
        this.executeTime = executeTime!=null?new Timestamp(executeTime.getTime()):null;
    }

    public Boolean getRepeatFlag() {
        return repeatFlag==null?false:repeatFlag;
    }
}
