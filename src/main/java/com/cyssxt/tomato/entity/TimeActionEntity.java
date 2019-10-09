package com.cyssxt.tomato.entity;

import com.cyssxt.common.entity.BaseEntity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "time_action",indexes = {@Index(name = "time_action_index",columnList = "to_do_id,parent_id,date_no,type")}, schema = "tomato_project", catalog = "")
public class TimeActionEntity extends BaseEntity {
    private String rowId;
    private Boolean delFlag;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Timestamp startTime;
    private Timestamp endTime;
    private String toDoId;
    private Long totalTime;
    private String userId;
    private String parentId;
    private Byte type;
    private Integer dateNo;
    private Timestamp expireTime;
    private Boolean pushFlag;
    private Boolean lastFlag;
    private Long configTime;
    private Timestamp pushTime;
    private String actionId;
    private String childId;

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
    @Column(name = "start_time")
    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    @Basic
    @Column(name = "end_time")
    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    @Basic
    @Column(name = "to_do_id")
    public String getToDoId() {
        return toDoId;
    }

    public void setToDoId(String toDoId) {
        this.toDoId = toDoId;
    }

    @Basic
    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeActionEntity that = (TimeActionEntity) o;
        return Objects.equals(rowId, that.rowId) &&
                Objects.equals(delFlag, that.delFlag) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(toDoId, that.toDoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowId, delFlag, createTime, updateTime, startTime, endTime, toDoId,userId);
    }

    @Basic
    @Column(name = "total_time")
    public Long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Long totalTime) {
        this.totalTime = totalTime;
    }

    @Basic
    @Column(name = "parent_id")
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Basic
    @Column(name = "type")
    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    @Basic
    @Column(name = "date_no")
    public Integer getDateNo() {
        return dateNo;
    }

    public void setDateNo(Integer dateNo) {
        this.dateNo = dateNo;
    }

    @Basic
    @Column(name = "expire_time")
    public Timestamp getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Timestamp expireTime) {
        this.expireTime = expireTime;
    }

    @Basic
    @Column(name = "push_flag")
    public Boolean getPushFlag() {
        return pushFlag;
    }

    public void setPushFlag(Boolean pushFlag) {
        this.pushFlag = pushFlag;
    }

    @Basic
    @Column(name = "last_flag")
    public Boolean getLastFlag() {
        return lastFlag;
    }

    public void setLastFlag(Boolean lastFlag) {
        this.lastFlag = lastFlag;
    }

    @Basic
    @Column(name = "config_time")
    public Long getConfigTime() {
        return configTime;
    }

    public void setConfigTime(Long configTime) {
        this.configTime = configTime;
    }

    @Basic
    @Column(name = "push_time")
    public Timestamp getPushTime() {
        return pushTime;
    }

    public void setPushTime(Timestamp pushTime) {
        this.pushTime = pushTime;
    }

    @Basic
    @Column(name = "action_id")
    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    @Basic
    @Column(name = "child_id",length = 50)
    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }
}
