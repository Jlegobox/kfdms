package com.mp.kfdms.pojo;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/19
 * @Time 17:03
 */
public class RegisterInfo {
    private String email;
    private String password;
//    private String verification_code;
    private String time;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    public String getVerification_code() {
//        return verification_code;
//    }
//
//    public void setVerification_code(String verification_code) {
//        this.verification_code = verification_code;
//    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
