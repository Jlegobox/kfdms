package com.mp.kfdms.pojo;

/**
 * 创建分享链接时传入的参数列表
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/2/23
 * @Time 10:21
 */
public class FileShareLinkInfo {
    private String accessCode;
    private int visitLimit;
    private int validPeriod;
    private int folderId;
    private int fileId;

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
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

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    @Override
    public String toString() {
        return "FileShareLinkInfo{" +
                "accessCode='" + accessCode + '\'' +
                ", visitLimit=" + visitLimit +
                ", validPeriod=" + validPeriod +
                ", folderId=" + folderId +
                ", fileId=" + fileId +
                '}';
    }
}
