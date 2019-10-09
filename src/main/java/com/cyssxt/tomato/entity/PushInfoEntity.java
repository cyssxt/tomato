package com.cyssxt.tomato.entity;

import com.cyssxt.common.entity.BaseEntity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "push_info")
public class PushInfoEntity extends BaseEntity {
    private String rowId;
    private Boolean delFlag;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String title;
    private String content;
    private Byte contentType;
    private String userId;
    private String response;
    private String taskId;
    private String pushTitle;
    private String pushText;

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
    @Column(name = "content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "response")
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Basic
    @Column(name = "task_id")
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    @Basic
    @Column(name = "push_title")
    public String getPushTitle() {
        return pushTitle;
    }

    public void setPushTitle(String pushTitle) {
        this.pushTitle = pushTitle;
    }

    @Basic
    @Column(name = "push_text")
    public String getPushText() {
        return pushText;
    }

    public void setPushText(String pushText) {
        this.pushText = pushText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PushInfoEntity that = (PushInfoEntity) o;
        return Objects.equals(rowId, that.rowId) &&
                Objects.equals(delFlag, that.delFlag) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(title, that.title) &&
                Objects.equals(content, that.content) &&
                Objects.equals(contentType, that.contentType) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(response, that.response) &&
                Objects.equals(taskId, that.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowId, delFlag, createTime, updateTime, title, content, contentType, userId, response, taskId);
    }
}
