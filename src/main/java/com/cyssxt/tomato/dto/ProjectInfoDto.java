package com.cyssxt.tomato.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class ProjectInfoDto{
    private String rowId;
    private Boolean delFlag;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String title;
    private String remark;
    private Byte status;
    private String userId;
    private Integer sort;
    private String parentId;
    List<ProjectItemDto> sections;

}
