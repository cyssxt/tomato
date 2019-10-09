package com.cyssxt.tomato.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "project_info")
public class ProjectInfoEntity extends ActionEntity implements MoveInterface {
    private String rowId;
    private Boolean delFlag;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String title;
    private String remark;
    private Byte status;
    private String userId;
    private Integer sort;
    private String parentId;
    private Timestamp finishTime;
    private Integer dateNo;
    private Timestamp executeTime;
    private Timestamp endTime;
    private Byte parentType;

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
    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Basic
    @Column(name = "status")
    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
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
    @Column(name = "sort")
    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectInfoEntity that = (ProjectInfoEntity) o;
        return Objects.equals(rowId, that.rowId) &&
                Objects.equals(delFlag, that.delFlag) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(title, that.title) &&
                Objects.equals(remark, that.remark) &&
                Objects.equals(status, that.status) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(sort, that.sort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowId, delFlag, createTime, updateTime, title, remark, status, userId, sort);
    }

    @Override
    @Basic
    @Column(name="parent_type")
    public Byte getParentType() {
        return parentType;
    }

    @Basic
    @Column(name = "parent_id")
    public String getParentId() {
        return parentId;
    }

    @Override
    public void setParentType(Byte parentType) {
        this.parentType = parentType;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Basic
    @Column(name = "finish_time")
    public Timestamp getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Timestamp finishTime) {
        this.finishTime = finishTime;
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
    @Column(name = "execute_time")
    public Timestamp getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Timestamp executeTime) {
        this.executeTime = executeTime;
    }

    @Basic
    @Column(name = "end_time")
    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }
}
