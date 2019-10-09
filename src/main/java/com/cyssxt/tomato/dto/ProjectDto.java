package com.cyssxt.tomato.dto;

import lombok.Data;

@Data
public class ProjectDto {

    private String rowId;
    private String title;
    private String remark;
    private Byte status;
    private Integer sort;
    private String parentId;
    private String parentTitle;
    private Byte contentType=1;
    public ProjectDto(){}
    public ProjectDto(String rowId, String title, String remark, Byte status, Integer sort, String parentId, String parentTitle) {
        this.rowId = rowId;
        this.title = title;
        this.remark = remark;
        this.status = status;
        this.sort = sort;
        this.parentId = parentId;
        this.parentTitle = parentTitle;
    }

    public ProjectDto(String rowId, String title, String remark, Byte status, Integer sort) {
        this.rowId = rowId;
        this.title = title;
        this.remark = remark;
        this.status = status;
        this.sort = sort;
    }
}
