package com.cyssxt.tomato.entity;

import com.cyssxt.common.entity.BaseEntity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "duty_section",uniqueConstraints = {@UniqueConstraint(name="duty_unique_index",columnNames = {"content_id","content_type","duty_id"})})
public class DutySectionEntity extends BaseEntity {
    private String rowId;
    private Boolean delFlag;
    private Timestamp updateTime;
    private Timestamp createTime;
    private String contentId;
    private Byte contentType;
    private String dutyId;
    private Integer sort;

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
    @Column(name = "update_time")
    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
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
    @Column(name = "content_id")
    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    @Basic
    @Column(name = "content_type")
    public Byte getContentType() {
        return contentType;
    }

    public void setContentType(Byte contentType) {
        this.contentType = contentType;
    }

    @Basic
    @Column(name = "duty_id")
    public String getDutyId() {
        return dutyId;
    }

    public void setDutyId(String dutyId) {
        this.dutyId = dutyId;
    }

    @Basic
    @Column(name="sort")
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
        DutySectionEntity that = (DutySectionEntity) o;
        return Objects.equals(rowId, that.rowId) &&
                Objects.equals(delFlag, that.delFlag) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(contentId, that.contentId) &&
                Objects.equals(contentType, that.contentType) &&
                Objects.equals(dutyId, that.dutyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowId, delFlag, updateTime, createTime, contentId, contentType, dutyId);
    }
}
