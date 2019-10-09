package com.cyssxt.tomato.entity;

import com.cyssxt.common.entity.BaseEntity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "to_do_day_info",uniqueConstraints = {@UniqueConstraint(name="to_do_day_info_unique",columnNames = {"to_do_id","date_no"})},indexes = {@Index(name="to_do_day_info_index",columnList = "date_no,to_do_id,user_id,status")}, schema = "tomato_project", catalog = "")
public class ToDoDayInfoEntity extends BaseEntity {
    private String rowId;
    private Boolean delFlag;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String toDoId;
    private Integer dateNo;
    private Timestamp executeTime;
    private String generateDay;
    private Integer generateSequence;
    private String userId;
    private Byte status;
    private Timestamp finishTime;
    private Integer concentrationDegree;
    private Byte finishDegree;
    private Integer consumTime;
    private Boolean timeFlag;
    private Boolean pushFlag;
    private Timestamp pushTime;

    public ToDoDayInfoEntity(){
        this.setStatus((byte)0);
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
    @Column(name = "to_do_id")
    public String getToDoId() {
        return toDoId;
    }

    public void setToDoId(String toDoId) {
        this.toDoId = toDoId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToDoDayInfoEntity that = (ToDoDayInfoEntity) o;
        return Objects.equals(rowId, that.rowId) &&
                Objects.equals(delFlag, that.delFlag) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(toDoId, that.toDoId) &&
                Objects.equals(dateNo, that.dateNo) &&
                Objects.equals(executeTime, that.executeTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowId, delFlag, createTime, updateTime, toDoId, dateNo, executeTime);
    }

    @Basic
    @Column(name = "generate_day")
    public String getGenerateDay() {
        return generateDay;
    }

    public void setGenerateDay(String generateDay) {
        this.generateDay = generateDay;
    }

    @Basic
    @Column(name = "generate_sequence")
    public Integer getGenerateSequence() {
        return generateSequence;
    }

    public void setGenerateSequence(Integer generateSequence) {
        this.generateSequence = generateSequence;
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
    @Column(name = "status")
    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
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
    @Column(name = "concentration_degree")
    public Integer getConcentrationDegree() {
        return concentrationDegree;
    }

    public void setConcentrationDegree(Integer concentrationDegree) {
        this.concentrationDegree = concentrationDegree;
    }

    @Basic
    @Column(name = "finish_degree")
    public Byte getFinishDegree() {
        return finishDegree;
    }

    public void setFinishDegree(Byte finishDegree) {
        this.finishDegree = finishDegree;
    }

    @Basic
    @Column(name = "consum_time")
    public Integer getConsumTime() {
        return consumTime;
    }

    public void setConsumTime(Integer consumTime) {
        this.consumTime = consumTime;
    }

    @Basic
    @Column(name = "time_flag")
    public Boolean getTimeFlag() {
        return timeFlag;
    }

    public void setTimeFlag(Boolean timeFlag) {
        this.timeFlag = timeFlag;
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
    @Column(name = "push_time")
    public Timestamp getPushTime() {
        return pushTime;
    }

    public void setPushTime(Timestamp pushTime) {
        this.pushTime = pushTime;
    }
}
