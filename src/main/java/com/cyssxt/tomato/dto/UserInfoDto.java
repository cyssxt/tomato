package com.cyssxt.tomato.dto;

import com.cyssxt.tomato.entity.UserInfoEntity;
import lombok.Data;

@Data
public class UserInfoDto {
    private String banner;
    private String userName;
    private String userIcon;
    private String introduce;
    private Long time;
    private Long total;

    public UserInfoDto(UserInfoEntity userInfoEntity) {
        this.banner = userInfoEntity.getBanner();
        this.userName = userInfoEntity.getUserName();
        this.introduce = userInfoEntity.getIntroduce();
        this.userIcon = userInfoEntity.getUserIcon();
    }

    public UserInfoDto(UserInfoEntity userInfoEntity, TodoInfo todoInfo) {
        this(userInfoEntity);
        if(todoInfo!=null) {
            this.time = todoInfo.getTime();
            this.total = todoInfo.getTotal();
        }
    }
}
