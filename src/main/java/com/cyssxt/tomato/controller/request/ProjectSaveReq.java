package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.BaseReq;
import com.cyssxt.tomato.dto.ProjectItem;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

@Data
public class ProjectSaveReq extends BaseReq {
    @NotNull
    @NotEmpty
    String contentId;
    private List<ProjectItem> projectItems;
    @NotNull
    private String title;
    @NotNull
    private String remark;
    @NotNull
    private Byte status;
    private List<String> tagIds;
    private Timestamp executeTime;
    private Timestamp endTime;
}
