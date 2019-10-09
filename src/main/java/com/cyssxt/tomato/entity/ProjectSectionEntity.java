package com.cyssxt.tomato.entity;

import com.cyssxt.common.entity.BaseEntity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "project_section",uniqueConstraints = {@UniqueConstraint(name="project_secion_unique",columnNames = {"project_id","to_do_id","type","title"})}, schema = "tomato_project", catalog = "")
public class ProjectSectionEntity extends BaseEntity {
    private String rowId;
    private Boolean delFlag;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String title;
    private String projectId;
    private Integer sort;
    private Byte type;
    private String todoId;

    @Override
    public String[] defineExcludes() {
        return new String[]{"delFlag"};
    }

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
    @Column(name = "project_id")
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectSectionEntity that = (ProjectSectionEntity) o;
        return Objects.equals(rowId, that.rowId) &&
                Objects.equals(delFlag, that.delFlag) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(title, that.title) &&
                Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowId, delFlag, createTime, updateTime, title, projectId);
    }

    @Basic
    @Column(name = "sort")
    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Basic
    @Column(name="type")
    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    @Basic
    @Column(name="to_do_id")
    public String getTodoId() {
        return todoId;
    }

    public void setTodoId(String todoId) {
        this.todoId = todoId;
    }
}
