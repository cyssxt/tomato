package com.cyssxt.tomato.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class UserLogDto {
    private String rowId;
    private Date createTime;
    private String title;
    private String introduce;
    private String imgUrl;

    public UserLogDto(){}

    public UserLogDto(String rowId, Date createTime, String title, String introduce, String imgUrl) {
        this.rowId = rowId;
        this.createTime = createTime;
        this.title = title;
        this.introduce = introduce;
        this.imgUrl = imgUrl;
    }
}
