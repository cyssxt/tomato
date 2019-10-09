package com.cyssxt.tomato.dto;

import com.cyssxt.common.constant.CharConstant;
import lombok.Data;

@Data
public class UserSessionDto {
    String sessionId;
    String userId;
    Byte userFlag;
    String sessionValue;

    public UserSessionDto(String sessionId, String sessionValue) {
        this.sessionId = sessionId;
        this.sessionValue = sessionValue;
        String[] infos = sessionValue.split(CharConstant.UNDERLINE);
        if(infos.length>0){
            userId = infos[0];
        }
        if(infos.length>1){
            userFlag = Byte.valueOf(infos[1]);
        }
    }
}
