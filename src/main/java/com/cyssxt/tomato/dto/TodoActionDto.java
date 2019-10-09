package com.cyssxt.tomato.dto;

import lombok.Data;

@Data
public class TodoActionDto {

    private String rowId;
    private String title;
    private Byte status;
    private String parentId;

    public TodoActionDto(String rowId, String title, Byte status) {
        this.rowId = rowId;
        this.title = title;
        this.status = status;
    }

    public TodoActionDto(String rowId, String title, Byte status, String parentId) {
        this.rowId = rowId;
        this.title = title;
        this.status = status;
        this.parentId = parentId;
    }
}
