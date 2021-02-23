package com.mp.kfdms.domain;

import java.util.Date;

/**
 * 文件分享的分享链接日志
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/2/23
 * @Time 9:45
 */
public class FileShareShareLog {
    private int shareLogId;
    private int userId;
    private int fileId;
    private int folderId;
    private String accessCode;
    private int visitNum; // 已访问数量
    private int visitLimit; // 可访问上限
    private int validPeriod; // 有效期 单位为天 -1 为无限
    private Date createTime;
    private Date expiredTime;
    private Date deleteTime; // 手动取销或过期后选择不展示的日期
    private int status;
    private int version;

    public int getShareLogId() {
        return shareLogId;
    }

    public void setShareLogId(int shareLogId) {
        this.shareLogId = shareLogId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public int getVisitNum() {
        return visitNum;
    }

    public void setVisitNum(int visitNum) {
        this.visitNum = visitNum;
    }

    public int getVisitLimit() {
        return visitLimit;
    }

    public void setVisitLimit(int visitLimit) {
        this.visitLimit = visitLimit;
    }

    public int getValidPeriod() {
        return validPeriod;
    }

    public void setValidPeriod(int validPeriod) {
        this.validPeriod = validPeriod;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "FileShareShareLog{" +
                "shareLogId=" + shareLogId +
                ", userId=" + userId +
                ", fileId=" + fileId +
                ", folderId=" + folderId +
                ", accessCode='" + accessCode + '\'' +
                ", visitNum=" + visitNum +
                ", visitLimit=" + visitLimit +
                ", validPeriod=" + validPeriod +
                ", createTime=" + createTime +
                ", expiredTime=" + expiredTime +
                ", deleteTime=" + deleteTime +
                ", status=" + status +
                ", version=" + version +
                '}';
    }
}
