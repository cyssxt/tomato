package com.cyssxt.tomato.controller.request;

import com.cyssxt.common.request.BaseReq;
import com.cyssxt.tomato.dto.DutySectionItem;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DutySaveReq extends DutyCreateReq {
    @NotNull
    @NotEmpty
    String contentId;
    List<DutySectionItem> items;
    List<String> tagIds;
}
