package com.cyssxt.tomato.entity;

import com.cyssxt.common.entity.BaseEntity;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.tomato.errors.MessageCode;
import com.cyssxt.tomato.service.ActionService;
import lombok.Data;

import java.beans.Transient;
import java.sql.Timestamp;

@Data
public abstract class ActionEntity extends BaseEntity {
    String userId;
    String parentId;
    Byte status;
    String rowId;
    Timestamp finishTime;
    Timestamp startTime;
    public Byte getParentType(){
        return ActionService.DETAULT_NOT_TYPE;
    }

}

