package com.mp.kfdms.domain;

import java.util.Date;

/**
 * 分享链接
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/3/3
 * @Time 20:16
 */
public class VerificationLog {
    private int verificationId;
    private int verificationOwner;
    private String verificationCode;
    private Date createTime;
    private int isUsed;
    private Date usedTime;
    private String usedUserEmail;

    public int getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(int verificationId) {
        this.verificationId = verificationId;
    }

    public int getVerificationOwner() {
        return verificationOwner;
    }

    public void setVerificationOwner(int verificationOwner) {
        this.verificationOwner = verificationOwner;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(int isUsed) {
        this.isUsed = isUsed;
    }

    public Date getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(Date usedTime) {
        this.usedTime = usedTime;
    }

    public String getUsedUserEmail() {
        return usedUserEmail;
    }

    public void setUsedUserEmail(String usedUserEmail) {
        this.usedUserEmail = usedUserEmail;
    }
}
