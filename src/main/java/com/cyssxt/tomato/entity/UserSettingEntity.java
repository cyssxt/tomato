package com.cyssxt.tomato.entity;

import com.cyssxt.common.entity.BaseEntity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "user_setting")
public class UserSettingEntity extends BaseEntity {
    private String rowId;
    private Boolean delFlag;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Byte remindType;
    private String userId;
    private Byte timeType;
    private Integer timeValue;
    private Boolean orderCal;

    @Id
    @Column(name = "row_id")
    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    @Basic
    @Column(name = "del_flag")
    public Boolean getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Boolean delFlag) {
        this.delFlag = delFlag;
    }

    @Basic
    @Column(name = "create_time")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Basic
    @Column(name = "update_time")
    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Basic
    @Column(name = "remind_type")
    public Byte getRemindType() {
        return remindType;
    }

    public void setRemindType(Byte remindType) {
        this.remindType = remindType;
    }

    @Basic
    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "time_type")
    public Byte getTimeType() {
        return timeType;
    }

    public void setTimeType(Byte timeType) {
        this.timeType = timeType;
    }

    @Basic
    @Column(name = "time_value")
    public Integer getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(Integer timeValue) {
        this.timeValue = timeValue;
    }

    @Basic
    @Column(name = "order_cal")
    public Boolean getOrderCal() {
        return orderCal;
    }

    public void setOrderCal(Boolean orderCal) {
        this.orderCal = orderCal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSettingEntity that = (UserSettingEntity) o;
        return Objects.equals(rowId, that.rowId) &&
                Objects.equals(delFlag, that.delFlag) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(remindType, that.remindType) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(timeType, that.timeType) &&
                Objects.equals(timeValue, that.timeValue) &&
                Objects.equals(orderCal, that.orderCal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowId, delFlag, createTime, updateTime, remindType, userId, timeType, timeValue, orderCal);
    }
}
