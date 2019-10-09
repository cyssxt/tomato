package com.cyssxt.tomato.entity;

import java.sql.Timestamp;

public interface MoveInterface {
    Byte getParentType();
    String getParentId();
    void setParentType(Byte type);
    void setUpdateTime(Timestamp updateTime);
}
