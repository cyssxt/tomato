package com.cyssxt.tomato.entity;

import com.cyssxt.common.entity.BaseEntity;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "user_info",uniqueConstraints = {@UniqueConstraint(name = "user_info_unique",columnNames = {"phone_number"})})
public class UserInfoEntity extends BaseEntity {
    private String rowId;
    private Boolean delFlag;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String userName;
    private String userIcon;
    private String phoneNumber;
    private Timestamp lastLoginTime;
    private String wxId;
    private String sinaId;
    private String qqId;
    private String introduce;
    private String banner;
    private Boolean guestFlag;

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
    @Column(name = "user_name")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Basic
    @Column(name = "user_icon")
    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    @Basic
    @Column(name = "phone_number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Basic
    @Column(name = "last_login_time")
    public Timestamp getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Timestamp lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    @Basic
    @Column(name = "wx_id")
    public String getWxId() {
        return wxId;
    }

    public void setWxId(String wxId) {
        this.wxId = wxId;
    }

    @Basic
    @Column(name = "sina_id")
    public String getSinaId() {
        return sinaId;
    }

    public void setSinaId(String sinaId) {
        this.sinaId = sinaId;
    }

    @Basic
    @Column(name = "qq_id")
    public String getQqId() {
        return qqId;
    }

    public void setQqId(String qqId) {
        this.qqId = qqId;
    }

    @Basic
    @Column(name = "introduce")
    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    @Basic
    @Column(name="banner")
    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    @Basic
    @Column(name="guest_flag")
    public Boolean getGuestFlag() {
        return guestFlag;
    }

    public void setGuestFlag(Boolean guestFlag) {
        this.guestFlag = guestFlag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfoEntity that = (UserInfoEntity) o;
        return Objects.equals(rowId, that.rowId) &&
                Objects.equals(delFlag, that.delFlag) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(userIcon, that.userIcon) &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(lastLoginTime, that.lastLoginTime) &&
                Objects.equals(wxId, that.wxId) &&
                Objects.equals(sinaId, that.sinaId) &&
                Objects.equals(qqId, that.qqId) &&
                Objects.equals(introduce, that.introduce);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowId, delFlag, createTime, updateTime, userName, userIcon, phoneNumber, lastLoginTime, wxId, sinaId, qqId, introduce);
    }
}
