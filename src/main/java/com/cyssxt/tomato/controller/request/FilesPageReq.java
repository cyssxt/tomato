package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.PageReq;
import lombok.Data;

import java.util.List;

@Data
public class FilesPageReq extends PageReq {
    Integer startDateNo;
    Integer endDateNo;
    Integer monthNo;
    List<String> tagIds;
}
