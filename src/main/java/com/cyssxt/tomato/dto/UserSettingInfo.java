package com.cyssxt.tomato.dto;

import com.cyssxt.tomato.constant.ReminderTypeConstant;
import com.cyssxt.tomato.constant.TimeingTypeConstant;
import com.cyssxt.tomato.entity.GoalsEntity;
import com.cyssxt.tomato.entity.UserInfoEntity;
import com.cyssxt.tomato.entity.UserSettingEntity;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class UserSettingInfo {
    private Boolean wxFlag;
    private Boolean qqFlag;
    private Boolean sinaFlag;
    private Byte remindType;
    private Boolean orderCal;
    private Byte timeType;
    private Integer timeValue;
    private String yearGoals;

    public UserSettingInfo(UserSettingEntity userSettingEntity, UserInfoEntity userInfoEntity, GoalsEntity goalsEntity) {
        this.wxFlag = StringUtils.isEmpty(userInfoEntity.getWxId());
        this.qqFlag = StringUtils.isEmpty(userInfoEntity.getQqId());
        this.sinaFlag = StringUtils.isEmpty(userInfoEntity.getSinaId());
        this.remindType = userSettingEntity.getRemindType()==null? ReminderTypeConstant.ON_TIME.getValue():userSettingEntity.getRemindType();
        this.orderCal = userSettingEntity.getOrderCal()==null?false:userSettingEntity.getOrderCal();
        this.timeType = userSettingEntity.getTimeType()==null? TimeingTypeConstant.NEG.getValue():userSettingEntity.getTimeType();
        this.timeValue = userSettingEntity.getTimeValue()==null?25:userSettingEntity.getTimeValue();
        this.yearGoals = goalsEntity!=null?goalsEntity.getGlobalGoal():"";
    }
}
