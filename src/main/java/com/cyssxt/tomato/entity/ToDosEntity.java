package com.cyssxt.tomato.entity;

import com.cyssxt.tomato.constant.TaskStatusConstant;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "to_dos",indexes = {@Index(name="to_do_index",columnList = "parent_id,parent_type,status,execute_time")})
public class ToDosEntity extends ActionEntity implements MoveInterface{
    private String rowId;
    private Boolean delFlag;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String title;
    private Byte status;
    private Timestamp executeTime;
    private String repeatId;
    private Boolean remindFlag;
    private String userId;
    private String parentId;
    private Byte parentType;
    private String smallTitle;
    private Integer planTime;
    private Integer concentrationDegree;
    private String smallId;
    private Integer sort;
    private String content;
    private Timestamp nextExecuteTime;
    private Boolean repeatFlag;
    private Byte repeatType;
    private Byte repeatUnit;
    private Byte repeatEndType;
    private Timestamp repeatEndTime;
    private String repeatExecDay;
    private Integer repeatUnitValue;
    private Timestamp finishTime;
    private Timestamp endTime;
    private Boolean tagFlag;
    private Boolean actionFlag;
    private Boolean timeFlag;
    private Byte finishDegree;
    private Time dayTime;
    private Date dayDate;
    private Long consumeTime;
    private Boolean pushFlag;
    private Timestamp pushTime;
    private Integer dateNo;
    private Integer generator;
    private Boolean showFlag = true;
    private Boolean endFlag;



    public ToDosEntity(){
        this.setStatus(TaskStatusConstant.WAIT.getValue());
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
    @Column(name="finish_degree")
    public Byte getFinishDegree() {
        return finishDegree;
    }

    public void setFinishDegree(Byte finishDegree) {
        this.finishDegree = finishDegree;
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
    @Column(name = "status")
    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
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
    @Column(name = "repeat_id")
    public String getRepeatId() {
        return repeatId;
    }

    public void setRepeatId(String repeatId) {
        this.repeatId = repeatId;
    }

    @Basic
    @Column(name = "remind_flag")
    public Boolean getRemindFlag() {
        return remindFlag;
    }

    public void setRemindFlag(Boolean remindFlag) {
        this.remindFlag = remindFlag;
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
    @Column(name = "parent_id")
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Basic
    @Column(name = "parent_type")
    public Byte getParentType() {
        return parentType;
    }

    public void setParentType(Byte parentType) {
        this.parentType = parentType;
    }

    @Basic
    @Column(name = "small_title")
    public String getSmallTitle() {
        return smallTitle;
    }

    public void setSmallTitle(String smallTitle) {
        this.smallTitle = smallTitle;
    }

    @Basic
    @Column(name = "plan_time")
    public Integer getPlanTime() {
        return planTime;
    }

    public void setPlanTime(Integer planTime) {
        this.planTime = planTime;
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
    @Column(name = "small_id")
    public String getSmallId() {
        return smallId;
    }

    public void setSmallId(String smallId) {
        this.smallId = smallId;
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
    @Column(name = "content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Basic
    @Column(name="next_execute_time")
    public Timestamp getNextExecuteTime() {
        return nextExecuteTime;
    }

    public void setNextExecuteTime(Timestamp nextExecuteTime) {
        this.nextExecuteTime = nextExecuteTime;
    }

    @Basic
    @Column(name="end_time")
    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToDosEntity that = (ToDosEntity) o;
        return Objects.equals(rowId, that.rowId) &&
                Objects.equals(delFlag, that.delFlag) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(title, that.title) &&
                Objects.equals(status, that.status) &&
                Objects.equals(executeTime, that.executeTime) &&
                Objects.equals(repeatId, that.repeatId) &&
                Objects.equals(remindFlag, that.remindFlag) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(parentId, that.parentId) &&
                Objects.equals(parentType, that.parentType) &&
                Objects.equals(smallTitle, that.smallTitle) &&
                Objects.equals(planTime, that.planTime) &&
                Objects.equals(concentrationDegree, that.concentrationDegree) &&
                Objects.equals(smallId, that.smallId) &&
                Objects.equals(sort, that.sort) &&
                Objects.equals(dayDate, that.dayDate) &&
                Objects.equals(dayTime, that.dayTime) &&
                Objects.equals(finishDegree, that.finishDegree) &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowId, delFlag, createTime, updateTime, title, status, executeTime, repeatId, remindFlag, userId, parentId, parentType, smallTitle, planTime, concentrationDegree, smallId, sort, content);
    }

    @Basic
    @Column(name = "repeat_flag")
    public Boolean getRepeatFlag() {
        return repeatFlag;
    }

    public void setRepeatFlag(Boolean repeatFlag) {
        this.repeatFlag = repeatFlag;
    }

    @Basic
    @Column(name = "repeat_type")
    public Byte getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(Byte repeatType) {
        this.repeatType = repeatType;
    }

    @Basic
    @Column(name = "repeat_unit")
    public Byte getRepeatUnit() {
        return repeatUnit;
    }

    public void setRepeatUnit(Byte repeatUnit) {
        this.repeatUnit = repeatUnit;
    }

    @Basic
    @Column(name = "repeat_end_type")
    public Byte getRepeatEndType() {
        return repeatEndType;
    }

    public void setRepeatEndType(Byte repeatEndType) {
        this.repeatEndType = repeatEndType;
    }

    @Basic
    @Column(name = "repeat_end_time")
    public Timestamp getRepeatEndTime() {
        return repeatEndTime;
    }

    public void setRepeatEndTime(Timestamp repeatEndTime) {
        this.repeatEndTime = repeatEndTime;
    }

    @Basic
    @Column(name = "repeat_exec_day")
    public String getRepeatExecDay() {
        return repeatExecDay;
    }

    public void setRepeatExecDay(String repeatExecDay) {
        this.repeatExecDay = repeatExecDay;
    }

    @Basic
    @Column(name = "repeat_unit_value")
    public Integer getRepeatUnitValue() {
        return repeatUnitValue;
    }

    public void setRepeatUnitValue(Integer repeatUnitValue) {
        this.repeatUnitValue = repeatUnitValue;
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
    @Column(name = "tag_flag")
    public Boolean getTagFlag() {
        return tagFlag;
    }

    public void setTagFlag(Boolean tagFlag) {
        this.tagFlag = tagFlag;
    }

    @Basic
    @Column(name = "action_flag")
    public Boolean getActionFlag() {
        return actionFlag;
    }

    public void setActionFlag(Boolean actionFlag) {
        this.actionFlag = actionFlag;
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
    @Column(name="day_time")
    public Time getDayTime() {
        return dayTime;
    }

    public void setDayTime(Time dayTime) {
        this.dayTime = dayTime;
    }

    @Basic
    @Column(name="day_date")
    public Date getDayDate() {
        return dayDate;
    }

    public void setDayDate(Date dayDate) {
        this.dayDate = dayDate;
    }

    @Basic
    @Column(name="consume_time")
    public Long getConsumeTime() {
        return consumeTime;
    }

    public void setConsumeTime(Long consumeTime) {
        this.consumeTime = consumeTime;
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

    @Basic
    @Column(name="date_no")
    public Integer getDateNo() {
        return dateNo;
    }

    public void setDateNo(Integer dateNo) {
        this.dateNo = dateNo;
    }

    @Basic
    @Column(name="generator")
    public Integer getGenerator() {
        return generator;
    }

    public void setGenerator(Integer generator) {
        this.generator = generator;
    }

    public boolean equalRepeat(ToDosEntity that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;
        return Objects.equals(repeatEndTime, that.repeatEndTime) &&
                Objects.equals(repeatEndType,that.repeatEndType) &&
                Objects.equals(repeatUnit,that.repeatUnit) &&
                Objects.equals(repeatType,that.repeatType) &&
                Objects.equals(repeatUnitValue,that.repeatUnitValue) &&
                Objects.equals(repeatExecDay,that.repeatExecDay);
    }

    @Basic
    @Column(name="show_flag")
    public Boolean getShowFlag() {
        return showFlag;
    }

    public void setShowFlag(Boolean showFlag) {
        this.showFlag = showFlag;
    }


    @Basic
    @Column(name="end_flag")
    public Boolean getEndFlag() {
        return endFlag;
    }

    public void setEndFlag(Boolean endFlag) {
        this.endFlag = endFlag;
    }
}
