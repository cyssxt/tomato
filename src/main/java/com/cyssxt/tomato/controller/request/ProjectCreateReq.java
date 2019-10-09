package com.cyssxt.tomato.controller.request;

import com.cyssxt.tomato.dto.ProjectItem;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

@Data
public class ProjectCreateReq extends ActionCreateReq {

    @NotNull
    @NotEmpty
    private String title;
    private String remark;
    private Byte status;
    String contentId;
    private List<ProjectItem> projectItems;
    private List<String> tagIds;
    private Timestamp executeTime;
    private Timestamp endTime;
    String dutyId;
}
