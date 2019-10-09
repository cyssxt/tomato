package com.cyssxt.tomato.dto;

import lombok.Data;

import java.util.List;

@Data
public class FinishItemDto {

    private String finishTime;
    private String title;
    private Byte status;
    Integer dayNo;
    String rowId;
    Byte type;
    Integer fullMonth;
    private List<TagDto> tags;
    private List<FinishItemDto> childs;
    String parentId;
}
